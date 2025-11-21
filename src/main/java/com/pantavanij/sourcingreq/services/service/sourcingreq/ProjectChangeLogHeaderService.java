package com.pantavanij.sourcingreq.services.service.sourcingreq;

import com.pantavanij.sourcingreq.services.domain.dto.*;
import com.pantavanij.sourcingreq.services.domain.entity.sourcingreq.*;
import org.springframework.core.io.*;
import org.springframework.stereotype.*;

import java.util.*;

public interface ProjectChangeLogHeaderService {
    ByteArrayResource downloadProjectHistory(String fileId);
    ByteArrayResource downloadFileCurrent();
    String deleteProjectChangeLogHeaderFile(String fileId);
}
