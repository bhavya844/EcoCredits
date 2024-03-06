package com.group1.ecocredit.services.implementations;

import com.group1.ecocredit.dto.PickupCancelRequest;
import com.group1.ecocredit.dto.PickupRequest;
import com.group1.ecocredit.dto.PickupStatusResponse;
import com.group1.ecocredit.models.PickupStatus;
import com.group1.ecocredit.models.*;
import com.group1.ecocredit.repositories.*;
import com.group1.ecocredit.services.PickupService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PickupServiceImpl implements PickupService {

    @Autowired
    private PickupRepository pickupRepository;

    @Autowired
    private WasteRepository wasteRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public void schedulePickup(PickupRequest pickupRequest, User user) {
        Optional<Status> statusOptional =
                statusRepository.findByValue(PickupStatus.SCHEDULED);
        if (statusOptional.isEmpty()) {
            throw new IllegalArgumentException("Status not found: " + PickupStatus.SCHEDULED);
        }

        Pickup pickup = new Pickup();
        pickup.setDateTime(LocalDateTime.parse(pickupRequest.getDateTime()));
        pickup.setStatus(statusOptional.get());
        pickup.setUser(user);

        Pickup savedPickup = pickupRepository.save(pickup);

        for (var w : pickupRequest.getWastes()) {
            Waste waste = new Waste();
            Optional<Category> categoryOptional =
                    categoryRepository.findByValue(w.getCategory());
            if (categoryOptional.isEmpty()) {
                throw new IllegalArgumentException("Category not found: " + w.getCategory());
            }
            Category category = categoryOptional.get();
            waste.setCategory(category);
            waste.setPickup(savedPickup);
            waste.setWeight(w.getWeight());
            wasteRepository.save(waste);
        }
        System.out.println("Pickup scheduled");
    }
    @Override
    public boolean cancelPickup(PickupCancelRequest pickupToCancel) {

        Optional<Pickup> pickupOptional = pickupRepository.findById(pickupToCancel.getId());
        if(pickupOptional.isEmpty()) return false;

        Optional<Status> statusCanceledOptional =
                statusRepository.findByValue(PickupStatus.CANCELED);
        if(statusCanceledOptional.isEmpty()) return false;

        Pickup pickup = pickupOptional.get();
        Status canceled = statusCanceledOptional.get();
        pickup.setStatus(canceled);
        pickupRepository.save(pickup);
        return true;
    }


    @Override
    public List<PickupStatusResponse> getPickupStatus(Long userId) {
        List<PickupStatusResponse> pickupStatusList = new ArrayList<>();

        List<Pickup> pickups = pickupRepository.findByUserId(userId);

        if (pickups.isEmpty()) {
            throw new IllegalArgumentException("No pickups found for the user");
        }

        for (Pickup pickup : pickups) {
            LocalDateTime pickupDateTime = pickup.getDateTime();
            String pickupDate = pickupDateTime.toLocalDate().toString();
            String pickupTime = pickupDateTime.toLocalTime().toString();
            String pickupStatus = pickup.getStatus().getValue();
            PickupStatusResponse response = new PickupStatusResponse(pickup.getId(), pickupStatus, pickupDate, pickupTime);
            pickupStatusList.add(response);
        }

        return pickupStatusList;
    }
}
