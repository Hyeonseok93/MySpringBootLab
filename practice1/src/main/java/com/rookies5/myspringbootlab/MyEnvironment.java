package com.rookies5.myspringbootlab;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyEnvironment {
    private String mode;
}
