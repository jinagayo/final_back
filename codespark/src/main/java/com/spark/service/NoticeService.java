package com.spark.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.repository.JoinRepository;
import com.spark.repository.NoticeRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepo;
}
