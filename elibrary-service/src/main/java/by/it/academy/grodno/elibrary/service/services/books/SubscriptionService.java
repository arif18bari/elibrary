package by.it.academy.grodno.elibrary.service.services.books;

import by.it.academy.grodno.elibrary.api.dao.BookJpaRepository;
import by.it.academy.grodno.elibrary.api.dao.SubscriptionJpaRepository;
import by.it.academy.grodno.elibrary.api.dao.UserJpaRepository;
import by.it.academy.grodno.elibrary.api.dto.books.SubscriptionRequest;
import by.it.academy.grodno.elibrary.api.dto.books.SubscriptionDto;
import by.it.academy.grodno.elibrary.api.dto.books.SubscriptionRequestCode;
import by.it.academy.grodno.elibrary.api.mappers.SubscriptionMapper;
import by.it.academy.grodno.elibrary.api.services.books.ISubscriptionService;
import by.it.academy.grodno.elibrary.entities.books.Book;
import by.it.academy.grodno.elibrary.entities.books.Subscription;
import by.it.academy.grodno.elibrary.entities.books.SubscriptionStatus;
import by.it.academy.grodno.elibrary.entities.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SubscriptionService implements ISubscriptionService {

    @Autowired
    private SubscriptionJpaRepository subscriptionJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private BookJpaRepository bookJpaRepository;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    @Override
    public List<SubscriptionDto> findAll() {
        return subscriptionMapper.toDtos(subscriptionJpaRepository.findAll());
    }

    @Override
    public Page<SubscriptionDto> findAll(Pageable pageable) {
        return subscriptionMapper.toPageDto( subscriptionJpaRepository.findAll(pageable));
    }

    @Override
    public Page<SubscriptionDto> findAll(Long userId, Integer statusCode, Pageable pageable) {
        if (userId == null && statusCode != null && statusCode != 5) {
            return subscriptionMapper.toPageDto(subscriptionJpaRepository.findAllByStatus(
                    SubscriptionStatus.getSubscriptionStatus(statusCode), pageable));
        } else if (statusCode == null && userId != null && statusCode != 5){
            return subscriptionMapper.toPageDto(subscriptionJpaRepository.findByUserId(userId, pageable));
        } else if (statusCode != null && statusCode != 5){
            return subscriptionMapper.toPageDto(subscriptionJpaRepository.findByStatusAndUserId(
                    SubscriptionStatus.getSubscriptionStatus(statusCode), userId, pageable));
        } else {
            return findAll(pageable);
        }
    }

    @Override
    public Optional<SubscriptionDto> findById(Long id) {
        return subscriptionJpaRepository.findById(id).map(s -> subscriptionMapper.toDto(s));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<Subscription> bookOptional = subscriptionJpaRepository.findById(id);
        if (bookOptional.isPresent() && bookOptional.get().getStatus().equals(SubscriptionStatus.COMPLETED)) {
            bookOptional.ifPresent(book -> subscriptionJpaRepository.delete(book));
        }
    }

    private static final int DEFAULT_BOOKING_COUNT = 1;
    private static final int DEFAULT_BOOKING_DAYS = 1;

    @Override
    @Transactional
    public Optional<SubscriptionDto> booking(SubscriptionRequest entityDto) {
        entityDto.setCount(DEFAULT_BOOKING_COUNT);
        entityDto.setDays(DEFAULT_BOOKING_DAYS);
        entityDto.setStatus(1);
        return createAndSave(entityDto);
    }

    @Override
    @Transactional
    public Optional<SubscriptionDto> create(SubscriptionRequest entityDto) {
        return createAndSave(entityDto);
    }

    private Optional<SubscriptionDto> createAndSave(SubscriptionRequest request) {
        Optional<Book> optionalBook = bookJpaRepository.findById(request.getBookId());
        Optional<User> optionalUser = userJpaRepository.findById(request.getUserId());
        if (optionalBook.isPresent() && optionalUser.isPresent()) {
            Book book = optionalBook.get();
            User user = optionalUser.get();
            if (book.isAvailable()) {
                int amountTaken = takeBook(book, request.getCount());
                Subscription subscription = Subscription.builder()
                        .status(SubscriptionStatus.BOOKING)
                        .created(LocalDateTime.now().withNano(0))
                        .deadline(LocalDateTime.now().withNano(0).plusDays(request.getDays()))
                        .returned(0)
                        .book(book)
                        .took(amountTaken)
                        .user(user)
                        .build();
                subscription = subscriptionJpaRepository.save(subscription);
                return Optional.of(subscription).map(s -> subscriptionMapper.toDto(s));
            }
        }
        return Optional.empty();
    }

    private int takeBook(Book book, int count) {
        int taken;
        if (book.getAvailableCount() >= count) {
            book.setAvailableCount(book.getAvailableCount() - count);
            taken = count;
        } else {
            taken = book.getAvailableCount();
            book.setAvailableCount(0);
        }
        return taken;
    }

    @Override
    @Transactional
    public Optional<SubscriptionDto> update(Long id, SubscriptionRequest request) {
        Optional<Subscription> optionalSubscription = subscriptionJpaRepository.findById(id);
        if (!optionalSubscription.isPresent()) {
            return Optional.empty();
        }
        Subscription subscription = optionalSubscription.get();
        SubscriptionRequestCode requestCode = SubscriptionRequestCode.getSubscriptionRequestCode(request.getStatus());

        switch (requestCode) {
            case TAKE_BOOK:
                prepareToTakeBookSubscription(subscription, request);
                break;
            case LEAVE_BOOK:
                prepareToLeaveBookSubscription(subscription, request);
                break;
            case EXTENDED_SUBSCRIPTION:
                prepareToExtendedSubscription(subscription, request);
                break;
        }
        return Optional.of(subscriptionMapper.toDto(subscriptionJpaRepository.save(subscription)));
    }

    private void prepareToExtendedSubscription(Subscription subscription, SubscriptionRequest request) {
        if (request.getDays() > 0) {
            subscription.setDeadline(subscription.getDeadline().plusDays(request.getDays()));
        }
    }

    private void prepareToLeaveBookSubscription(Subscription subscription, SubscriptionRequest request) {
        int debtBook = subscription.getTook() - subscription.getReturned();
        int returnsBook = request.getCount();
        if (debtBook >= returnsBook && (subscription.getStatus().equals(SubscriptionStatus.READING) ||
                subscription.getStatus().equals(SubscriptionStatus.READING_EXTENDED))) {
            leaveBook(subscription.getBook(), returnsBook);
            subscription.setReturned(subscription.getReturned() + returnsBook);
            if (debtBook == returnsBook) {
                subscription.setStatus(SubscriptionStatus.COMPLETED);
                subscription.setDeadline(LocalDateTime.now().withNano(0));
                subscription.getBook().setRating(subscription.getBook().getRating() + 1);
            } else {
                subscription.setStatus(SubscriptionStatus.READING_EXTENDED);
            }
        }
    }

    private void prepareToTakeBookSubscription(Subscription subscription, SubscriptionRequest request) {
        if (subscription.getStatus().equals(SubscriptionStatus.BOOKING) && subscription.getTook() >= request.getCount()) {
            Book book = subscription.getBook();
            int remainder = subscription.getTook() - request.getCount();
            leaveBook(book, remainder);
            subscription.setTook(request.getCount());
            subscription.setReturned(0);
            subscription.setStatus(SubscriptionStatus.READING);
            subscription.setCreated(LocalDateTime.now().withNano(0));
            subscription.setDeadline(LocalDateTime.now().withNano(0).plusDays(request.getDays()));
        }
    }

    private void leaveBook(Book book, int count) {
        book.setAvailableCount(book.getAvailableCount() + count);
    }
}
