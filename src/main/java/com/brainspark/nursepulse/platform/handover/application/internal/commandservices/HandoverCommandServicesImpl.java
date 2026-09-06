package com.brainspark.nursepulse.platform.handover.application.internal.commandservices;

import com.brainspark.nursepulse.platform.handover.application.commandservices.HandoverCommandService;
import com.brainspark.nursepulse.platform.handover.domain.model.aggregates.Handover;
import com.brainspark.nursepulse.platform.handover.domain.model.commands.CreateHandoverCommand;
import com.brainspark.nursepulse.platform.handover.domain.repositories.HandoverRepository;
import com.brainspark.nursepulse.platform.shared.application.result.ApplicationError;
import com.brainspark.nursepulse.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

@Service
public class HandoverCommandServicesImpl implements HandoverCommandService {
    private final HandoverRepository handoverRepository;

    public HandoverCommandServicesImpl(HandoverRepository handoverRepository) {
        this.handoverRepository = handoverRepository;
    }

    @Override
    public Result<Long, ApplicationError> handle(CreateHandoverCommand command) {
        var handover = new Handover(command);
        try {
            handover = handoverRepository.save(handover);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("create-handover", e.getMessage()));
        }
        return Result.success(handover.getId());
    }

    @Override
    public Result<Handover, ApplicationError> handle(com.brainspark.nursepulse.platform.handover.domain.model.commands.AcknowledgeHandoverCommand command) {
        var handoverOptional = handoverRepository.findById(command.handoverId());
        if (handoverOptional.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Handover", "Handover not found with ID %d".formatted(command.handoverId())));
        }
        var handover = handoverOptional.get();
        var createdAt = handover.getCreatedAt();
        handover.acknowledge(command.incomingNurseId(), command.additionalNotes());
        try {
            handover = handoverRepository.save(handover);
            // createdAt is @CreatedDate/updatable=false on the persistence entity
            // (no setter is exposed for it), so a merge-based update does not carry
            // it back on the returned, reconstructed domain object. Restore it here
            // so the acknowledge response still reports the real registration date.
            handover.setCreatedAt(createdAt);
        } catch (Exception e) {
            return Result.failure(ApplicationError.unexpected("acknowledge-handover", e.getMessage()));
        }
        return Result.success(handover);
    }
}