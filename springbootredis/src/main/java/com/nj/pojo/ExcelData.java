package com.nj.pojo;

import lombok.Data;

import java.util.List;

@Data
public class ExcelData {
    private String FileName;
    private String[] Head;
    private List<String[]> Data;
}
