package com.pantavanij.sourcingreq.services.service.sourcingreq.Impl;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import com.pantavanij.sourcingreq.services.enums.*;
import com.pantavanij.sourcingreq.services.exception.*;
import com.pantavanij.sourcingreq.services.repository.sourcingreq.*;
import com.pantavanij.sourcingreq.services.service.sourcingreq.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    @Override
    public List<DataSourceDto> getAllDataSource() {
        try {
            List<DataSource> dataSources = dataSourceRepository.findAll();
            if (!dataSources.isEmpty()) {
                // TODO: Can remove if it's store in data-source table.
                DataSource customeDataSource = DataSource.builder()
                        .recId(0)
                        .method(null)
                        .path(null)
                        .build();
                dataSources.add(customeDataSource);

                return dataSources.stream()
                        .map(dataSource -> {
                            DataSourceDto dataSourceDto = new DataSourceDto();
                            if (dataSource.getRecId() == 0) {
                                dataSourceDto.setRecId(0);
                                dataSourceDto.setMethod(dataSource.getMethod());
                                dataSourceDto.setPath(dataSource.getPath());
                                dataSourceDto.setName("n/a");
                                dataSourceDto.setLabel("N/A");
                                dataSourceDto.setValue(0);
                            } else {
                                String copyPath = dataSource.getPath();
                                copyPath = copyPath.substring(copyPath.lastIndexOf("/") + 1);
                                String name = copyPath;
                                copyPath = copyPath.substring(0, 1).toUpperCase() + copyPath.substring(1);
                                String label = dataSource.getMethod()+ "-" + copyPath;

                                dataSourceDto.setRecId(dataSource.getRecId());
                                dataSourceDto.setMethod(dataSource.getMethod());
                                dataSourceDto.setPath(dataSource.getPath());
                                dataSourceDto.setName(name);
                                dataSourceDto.setLabel(label);
                                dataSourceDto.setValue(dataSource.getRecId());
                            }
                            return dataSourceDto;
                        })
                        .collect(Collectors.toList());
            } else {
                throw new DataNotFoundException(String.format(ApiMessage.E7096.description(), "Data sources"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }
}
