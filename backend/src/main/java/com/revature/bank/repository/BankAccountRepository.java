package com.revature.bank.repository;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

import com.revature.bank.entity.BankAccount;

public interface BankAccountRepository
        extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByOwner_IdOrderByIdAsc(Long userId);

    //Helps enforce ownership:
    Optional<BankAccount> findByIdAndOwner_Id(
            Long accountId, Long userId);

    boolean existsByOwner_IdAndAccountType(
            Long userId, String accountType);
    //Requests a database lock on the matching account row.
    //Both IDs must match, so another user’s account won’t be returned.
    //We’ll call this from a service method marked @Transactional. The transaction keeps the lock
    //while we read, check, and update the balance, then releases it when the transaction commits
    // or rolls back.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    //Defines JPQL: it refers to Java entities and fields, such as BankAccount and owner.id.
    @Query("""
    SELECT account
    FROM BankAccount account
    WHERE account.id = :accountId
      AND account.owner.id = :userId
    """)
    Optional<BankAccount> findOwnedAccountForUpdate(
            //Connects each method argument to a named query parameter.
            @Param("accountId") Long accountId,
            @Param("userId") Long userId);
}
//Method	                        Meaning
//findByOwner_IdOrderByIdAsc	    Find this user’s accounts, ordered by account ID
//findByIdAndOwner_Id	            Find an account only when its ID and owner ID match
//existsByOwner_IdAndAccountType	Check whether this user already has that account type
//
//
//Owner_Id means “follow the owner relationship, then read its id field.” These names refer to Java entity fields, rather than SQL column names.
