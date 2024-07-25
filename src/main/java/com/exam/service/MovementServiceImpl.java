package com.exam.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.exam.dto.MovementDTO;
import com.exam.entity.Movement;
import com.exam.repository.MovementRepository;

@Service
@Transactional
public class MovementServiceImpl implements MovementService {
    
    private static final Logger logger = LoggerFactory.getLogger(MovementServiceImpl.class);

    private final MovementRepository movementRepository;

    public MovementServiceImpl(MovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    @Override
    public List<MovementDTO> findAll(){
        logger.debug("Request to find all movements");
        ModelMapper mapper = new ModelMapper();
        
        List<Movement> list = movementRepository.findAll();
        List<MovementDTO> movementList = list.stream()
                                              .map(e->mapper.map(e, MovementDTO.class))
                                              .collect(Collectors.toList());
        logger.debug("Movements found: {}", movementList);
        return movementList;
    }

    @Override
    public List<MovementDTO> findByOrdercode(Long ordercode){
        logger.debug("Request to find movements by ordercode: {}", ordercode);
        ModelMapper mapper = new ModelMapper();
        
        List<Movement> list = movementRepository.findByOrdercode(ordercode);
        List<MovementDTO> movementList = list.stream()
                                              .map(e->mapper.map(e, MovementDTO.class))
                                              .collect(Collectors.toList());
        logger.debug("Movements found for ordercode {}: {}", ordercode, movementList);
        return movementList;
    }

    @Override
    public List<MovementDTO> findGroupedByMovdate() {
        logger.debug("Request to find movements grouped by date");
        List<Object[]> results = movementRepository.findGroupedByMovdate();

        List<MovementDTO> groupedMovements = results.stream()
                                                    .map(result -> MovementDTO.builder()
                                                                              .movdate((LocalDate) result[0])
                                                                              .count((Long) result[1])
                                                                              .build())
                                                    .collect(Collectors.toList());
        logger.debug("Grouped movements found: {}", groupedMovements);
        return groupedMovements;
    }

    // 날짜순으로 모든 데이터찾기
    @Override
    public List<MovementDTO> findAllSortedByDate() {
        logger.debug("Request to find all movements sorted by date");
        ModelMapper mapper = new ModelMapper();
        
        List<Movement> list = movementRepository.findAllByOrderByMovdateAsc();
        List<MovementDTO> movementList = list.stream()
                                              .map(e -> mapper.map(e, MovementDTO.class))
                                              .collect(Collectors.toList());
        logger.debug("Sorted movements found: {}", movementList);
        return movementList;
    }

    // 날짜별로 데이터를 그룹화하여 반환
    @Override
    public Map<LocalDate, List<MovementDTO>> findAllGroupedByDate() {
        List<Movement> movements = movementRepository.findAll();
        ModelMapper mapper = new ModelMapper();
        
        List<MovementDTO> movementDTOs = movements.stream()
                .map(e -> mapper.map(e, MovementDTO.class))
                .collect(Collectors.toList());

        return movementDTOs.stream()
                .collect(Collectors.groupingBy(MovementDTO::getMovdate));
    }

}
