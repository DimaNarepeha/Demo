package com.softserve.demo.service.impl;

import com.softserve.demo.dto.FeedbackGeneralDTO;
import com.softserve.demo.repository.FeedbackRepository;
import com.softserve.demo.service.FeedbackService;
import com.softserve.demo.util.FeedbackGeneralMapper;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {


    final private FeedbackGeneralMapper feedbackMapper;
    final private FeedbackRepository feedbackRepository;

    public FeedbackServiceImpl(FeedbackGeneralMapper feedbackMapper, FeedbackRepository feedbackRepository) {
        this.feedbackMapper = feedbackMapper;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public long countByAddressedTo(Integer id) {
        return feedbackRepository.countByAddressedTo(id);
    }

    @Override
    public List<FeedbackGeneralDTO> findTop4ByCreatedDateBefore(Calendar createdDate) {
        return feedbackMapper.map(feedbackRepository.findTop4ByCreatedDateBefore(createdDate));
    }
}
