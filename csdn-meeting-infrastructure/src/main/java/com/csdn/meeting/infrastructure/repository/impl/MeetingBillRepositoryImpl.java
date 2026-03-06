package com.csdn.meeting.infrastructure.repository.impl;

import com.csdn.meeting.domain.entity.MeetingBill;
import com.csdn.meeting.domain.repository.MeetingBillRepository;
import com.csdn.meeting.infrastructure.po.MeetingBillPO;
import com.csdn.meeting.infrastructure.repository.MeetingBillJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MeetingBillRepositoryImpl implements MeetingBillRepository {

    private final MeetingBillJpaRepository jpaRepository;

    public MeetingBillRepositoryImpl(MeetingBillJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MeetingBill save(MeetingBill bill) {
        MeetingBillPO po = toPO(bill);
        MeetingBillPO saved = jpaRepository.save(po);
        return toEntity(saved);
    }

    @Override
    public List<MeetingBill> findByMeetingId(Long meetingId) {
        return jpaRepository.findByMeetingId(meetingId).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private MeetingBillPO toPO(MeetingBill e) {
        MeetingBillPO po = new MeetingBillPO();
        po.setId(e.getId());
        po.setMeetingId(e.getMeetingId());
        po.setFeeType(e.getFeeType());
        po.setAmount(e.getAmount());
        po.setPayStatus(e.getPayStatus());
        po.setInvoiceStatus(e.getInvoiceStatus());
        po.setCreatedAt(e.getCreatedAt());
        return po;
    }

    private MeetingBill toEntity(MeetingBillPO po) {
        MeetingBill e = new MeetingBill();
        e.setId(po.getId());
        e.setMeetingId(po.getMeetingId());
        e.setFeeType(po.getFeeType());
        e.setAmount(po.getAmount());
        e.setPayStatus(po.getPayStatus());
        e.setInvoiceStatus(po.getInvoiceStatus());
        e.setCreatedAt(po.getCreatedAt());
        return e;
    }
}
