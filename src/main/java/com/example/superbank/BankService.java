package com.example.superbank;

import com.example.superbank.model.TransferBalance;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class BankService {

    private final BalanceRepository repository;

    public BigDecimal getBalance(Long accountId){
        BigDecimal balance = repository.getBalanceForId(accountId);
        if(balance == null){
            throw new IllegalArgumentException();
        }
        return balance;
    }

    public BigDecimal addMoney(Long to, BigDecimal amount){
        final BigDecimal currentAmount = repository.getBalanceForId(to);
        if(currentAmount == null) {
            repository.save(to, amount);
            return amount;
        }else{
            repository.save(to, currentAmount.add(amount));
            return currentAmount.add(amount);
        }
    }

    public void makeTransfer(TransferBalance transferBalance){
        BigDecimal fromBalance = repository.getBalanceForId(transferBalance.getFrom());
        BigDecimal toBalance = repository.getBalanceForId(transferBalance.getTo());
        if(fromBalance == null || toBalance == null) throw new IllegalArgumentException("no money");
        if(transferBalance.getAmount().compareTo(fromBalance) > 0 ) throw new IllegalArgumentException("No money");

        BigDecimal updatedFromBalance = fromBalance.subtract(transferBalance.getAmount());
        BigDecimal updatedToBalance = fromBalance.add(transferBalance.getAmount());
        repository.save(transferBalance.getFrom(), updatedFromBalance);
        repository.save(transferBalance.getTo(), updatedToBalance);
    }
}
