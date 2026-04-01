package com.japaneseLearning.service;

import com.japaneseLearning.entity.Voucher;
import com.japaneseLearning.repository.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class VoucherService {
    
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public Voucher findByCode(String code) {
        return voucherRepository.findByCode(code);
    }

    public boolean isVoucherValid(String voucherCode) {
        Voucher voucher = voucherRepository.findByCode(voucherCode);
        if (voucher == null) {
            return false;
        }

        // Check expiration
        if (voucher.getExpireAt() != null && voucher.getExpireAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        // Check usage limit
        if (voucher.getMaxUsageCount() != null && 
            voucher.getCurrentUsageCount() >= voucher.getMaxUsageCount()) {
            return false;
        }

        return true;
    }

    public double applyVoucher(String voucherCode, double totalAmount) {
        Voucher voucher = voucherRepository.findByCode(voucherCode);
        if (voucher == null || !isVoucherValid(voucherCode)) {
            return totalAmount;
        }

        double discount = 0;
        if (voucher.getDiscountAmount() != null) {
            discount = voucher.getDiscountAmount();
        } else if (voucher.getDiscountPercentage() != null) {
            discount = totalAmount * voucher.getDiscountPercentage() / 100;
        }

        // Increment usage count
        voucher.setCurrentUsageCount(voucher.getCurrentUsageCount() + 1);
        voucherRepository.save(voucher);

        return Math.max(0, totalAmount - discount);
    }
}
