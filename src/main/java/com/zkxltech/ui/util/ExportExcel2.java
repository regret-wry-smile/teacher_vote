package com.zkxltech.ui.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.ejet.core.util.constant.Constant;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.zkxltech.domain.Record2;

@SuppressWarnings("deprecation")
public class ExportExcel2 {

    public static SXSSFWorkbook ExportWithResponse(String sheetName,String title, String dates,String className,String scenario,
    		int columnNumber, int[] columnWidth,String[] columnName, List<Map<String,List<Record2>>> arrlist) throws Exception {
        
    	SXSSFWorkbook wb = new SXSSFWorkbook(); 
        if (columnNumber == columnWidth.length&& columnWidth.length == columnName.length) {  
            // 第一步，创建一个webbook，对应一个Excel文件  
            
            // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
            Sheet sheet = wb.createSheet(sheetName);  
            // sheet.setDefaultColumnWidth(15); //统一设置列宽  
            for (int i = 0; i < columnNumber; i++)   
            {  
                for (int j = 0; j <= i; j++)   
                {  
                    if (i == j)   
                    {  
                        sheet.setColumnWidth(i, columnWidth[j] * 256*2); // 单独设置每列的宽  
                    }  
                }  
            }  
            // 创建第0行 也就是标题  
            Row row1 = sheet.createRow((int) 0);  
            row1.setHeightInPoints(20);// 设备标题的高度  
            // 第三步创建标题的单元格样式style2以及字体样式headerFont1  
            CellStyle style2 = wb.createCellStyle();  
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
            style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            Font headerFont =wb.createFont(); // 创建字体样式
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗
            headerFont.setFontName("黑体"); // 设置字体类型
            headerFont.setFontHeightInPoints((short) 16); // 设置字体大小
            style2.setFont(headerFont);
  
            Cell cell1 = row1.createCell(0);// 创建标题第一列  
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0,5)); // 合并列标题

            cell1.setCellStyle(style2); // 设置标题样式  
            cell1.setCellValue(title); // 设置创建标题

            //创建试卷名称列
            Row row2 = sheet.createRow((int) 1);  
            row2.setHeightInPoints(12);// 设备标题的高度  
            Cell cell2 = row2.createCell(0);// 创建标题第一列  
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0,5)); // 合并列
            cell2.setCellValue(dates); 
            
            //创建时间行
            Row row3 = sheet.createRow((int) 2);  
            row3.setHeightInPoints(12);// 设备标题的高度 
            Cell cell3 = row3.createCell(0);// 创建标题第一列  
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0,5)); // 合并列
            cell3.setCellValue(className); // 设置创建时间
            
            //班级名称和学生人数行
            Row row4 = sheet.createRow((int) 3);  
            row4.setHeightInPoints(12);// 设备标题的高度 
            Cell cell4 = row4.createCell(0);// 创建标题第一列  
            Cell cell5 = row4.createCell(4);// 创建标题第二列  
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0,5)); // 合并列
            cell4.setCellValue(scenario); // 设置班级名称

            
            // 创建第1行 也就是表头  
            Row row = sheet.createRow((int) 4);  
            row.setHeightInPoints(12);// 设置表头高度  
  
            // 第四步，创建表头单元格样式 以及表头的字体样式  
            CellStyle style = wb.createCellStyle();  
            style.setWrapText(true);// 设置自动换行  
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个居中格式  
  
            style.setBottomBorderColor(HSSFColor.BLACK.index);  
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);  
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);  
  
            //Font headerFont =wb.createFont(); // 创建字体样式
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 字体加粗  
            headerFont.setFontName("黑体"); // 设置字体类型  
            headerFont.setFontHeightInPoints((short) 10); // 设置字体大小  
            style.setFont(headerFont); // 为标题样式设置字体样式  
            
            /*正确的样式*/
            CellStyle trueStyle = wb.createCellStyle(); 
            trueStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
            trueStyle.setFillForegroundColor(HSSFColor.GREEN.index); 
            trueStyle.setWrapText(true);// 设置自动换行  
            trueStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个上下居中格式  
            trueStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中  
            trueStyle.setBottomBorderColor(HSSFColor.BLACK.index);  
            trueStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
            trueStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
            trueStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);  
            trueStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
            
            /*错误的样式*/
            CellStyle falseStyle = wb.createCellStyle();  
            falseStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); 
            falseStyle.setFillForegroundColor(HSSFColor.RED.index); 
            falseStyle.setWrapText(true);// 设置自动换行  
            falseStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个上下居中格式  
            falseStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中  
            falseStyle.setBottomBorderColor(HSSFColor.BLACK.index);  
            falseStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
            falseStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
            falseStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);  
            falseStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);  
  
            // 第四.一步，创建表头的列  
            for (int i = 0; i < columnNumber; i++)   
            {  
                Cell cell = row.createCell(i);  
                cell.setCellValue(columnName[i]);  
                cell.setCellStyle(style);  
            }

            // 第五步，创建单元格，并设置值
            int i=0;  int l=0;
            for(Map<String,List<Record2>> map : arrlist){  //1.获取list中每一个map,即得到每条数据
            	
                 // 为数据内容设置特点新单元格样式1 自动换行 上下居中  
                 CellStyle zidonghuanhang = wb.createCellStyle();  
                 zidonghuanhang.setWrapText(true);// 设置自动换行  
                 zidonghuanhang.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个居中格式  
   
                 // 设置边框  
                 zidonghuanhang.setBottomBorderColor(HSSFColor.BLACK.index);  
                 zidonghuanhang.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang.setBorderRight(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang.setBorderTop(HSSFCellStyle.BORDER_THIN);  
   
                 // 为数据内容设置特点新单元格样式2 自动换行 上下居中左右也居中  
                 CellStyle zidonghuanhang2 = wb.createCellStyle();  
                 zidonghuanhang2.setWrapText(true);// 设置自动换行  
                 zidonghuanhang2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // 创建一个上下居中格式  
                 zidonghuanhang2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中  
   
                 // 设置边框  
                 zidonghuanhang2.setBottomBorderColor(HSSFColor.BLACK.index);  
                 zidonghuanhang2.setBorderBottom(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang2.setBorderLeft(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang2.setBorderRight(HSSFCellStyle.BORDER_THIN);  
                 zidonghuanhang2.setBorderTop(HSSFCellStyle.BORDER_THIN);  
                 Cell datacell = null;  
               
                for(String key : map.keySet()){    //2.遍历每个map,得到含题号的list
                    String type = key;
                    String cellValue = "";
                    
                    List<Record2> list = map.get(key);
                    int j=0;
                    for(Record2 record4 : list){   //3.遍历含题号的list

                        List<Record2> list2 = record4.getDatalists();
                        int m = i+5;
                        if(m != (m+list2.size()-1)){
                            sheet.addMergedRegion(new CellRangeAddress(m, m+list2.size()-1, 1,1)); // 合并列
                        }
                        row = sheet.createRow(m);
                        datacell = row.createCell(1);
                        datacell.setCellStyle(zidonghuanhang2);
                        datacell.setCellValue(record4.getQuestionName());
                       
                        for(Record2 record5 : list2){   //4.遍历最后的dataList
                            if(m == (i+5)){
                                row = sheet.getRow(m);
                            }else {
                                row = sheet.createRow(i + 5);
                            }
                        	i++;
                            j++;

                            if(j==1){
                            	l=i;
                            }
                            String [] strArray = new String [4];
                            strArray[0] = ""+i;	//编号
                            strArray[1]=record5.getStudentName();
                            strArray[2]=record5.getAnswer();
                            strArray[3]=record5.getAnswerClick();
                            
                            for (int k = 0; k < strArray.length; k++) {
                            	
                            	 datacell = row.createCell(k+2);
                            	 cellValue=strArray[k];
                            	 datacell.setCellStyle(zidonghuanhang2);
                                 datacell.setCellValue(cellValue); 
							}
                        }
                    }
                    if((l+4) != (l+j+3)){
                    	sheet.addMergedRegion(new CellRangeAddress(l+4, l+j+3, 0,0)); // 合并列
                    }
                    row = sheet.getRow(l+4);
                    datacell = row.createCell(0);
                    datacell.setCellStyle(zidonghuanhang2);
                    datacell.setCellValue(type);
                }
                

            }
            // 第六步，将文件存到浏览器设置的下载位置  
           // String filename = fileName + ".xls";  
          
        } 
        return wb;
    }
}