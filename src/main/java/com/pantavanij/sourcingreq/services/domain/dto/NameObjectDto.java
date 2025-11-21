package com.pantavanij.sourcingreq.services.domain.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NameObjectDto<T> {
    private String label;
    private String name;
    private T value;
}
