/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coba2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import jxl.*;
import javax.swing.*;

/**
 *
 * @author USER
 */

public class Proses {
    private String[][] tmp;
    private int row;
    private int col;
    private DefaultTableModel table;
    private File f;
    private Sheet s;
    private Workbook wb;
    
    // untuk olah data
    private List kelas;
    private double[][] mean;
    private double[][] varians;
    private double[][] data_test;
    // total data tiap kelas
    private double[] total;
    private double totalData;
    
    public Proses(String file) throws Exception{
        this.f = new File(file);
        this.wb = Workbook.getWorkbook(f);
        this.s = wb.getSheet(0);
        this.row = s.getRows();
        this.col = s.getColumns();
        
        // membuat var berdasar row dan column
        this.tmp = new String[this.row][this.col];
        this.kelas = new ArrayList();
        // inisiasi kelas
        this.getKelas();
        // membuat mean 
        this.mean = new double[kelas.size()][this.col-1];
        this.varians = new double[kelas.size()][this.col-1];
        this.total = new double[kelas.size()];

        //System.out.println(mean[0][0]);
        //System.out.println(col);
        
        for(int i=0; i < row; i++){
            for(int j=0; j < col; j++){
                this.tmp[i][j] = s.getCell(j,i).getContents();
            }
        }
        
//        System.out.println(tmp[0][0]);
        
        for(int i=1; i<row; i++){
            int index = kelas.indexOf(tmp[i][0]);
            this.total[index] += 1;
            for(int j=1; j<col; j++){
                mean[index][j-1] += Integer.parseInt(tmp[i][j]);
            }
            System.out.println("");
        }
        
        // Buat bagi data total jumlah di var mean dengan data total perkelas di var total  
        for(int i=0; i<mean.length;i++){
            for(int j=0; j<mean[0].length;j++){
                mean[i][j] = mean[i][j] / total[i];
            }
        }
        
        // Hitung Varians
        for(int i=1; i<row; i++){
            int index = kelas.indexOf(tmp[i][0]);
            for(int j =1; j<col; j++){
                varians[index][j-1] += Math.pow((Double.parseDouble(tmp[i][j]) - mean[index][j-1]), 2.0)/(total[index]-1);
            }
        }
        
        // Buat uji coba
//        for(double[] m : this.totals) {
//            for(double n: m){
//                System.out.print(n + " ");
//            }
//            System.out.println("");
//        }

//          for(double m : total){
//              this.totalData += m;
//          }
//          System.out.println("________________________________________________" + totalData);
    }
    
    private void getKelas(){
        // mencari kelas dari data dengan malakukan looping pada column 0 dari row 1 dan memasukan ke list array
        for(int i=1; i < row; i++){
            for(int j=0; j < 1; j++){
                if(kelas.contains(s.getCell(j,i).getContents()) == false){
                    kelas.add(s.getCell(j,i).getContents());
                }
            }
        }
    }
    
    public void getColumn() throws Exception{
        DefaultTableModel tmp = new DefaultTableModel();
        for(String columnName : this.tmp[0]){
            tmp.addColumn(columnName);
        }
        this.table = tmp;
        //return tmp;
    }
    
    public void getFill() throws Exception{
        //int trig;
        for(int i=1; i<this.tmp.length; i++){
            Vector<Object> row = new Vector<Object>();
            for(String columnName : this.tmp[i]){
               row.add(columnName);
            }
            this.table.addRow(row);
        }
    }
    
    public DefaultTableModel getTable() throws Exception{
        this.getColumn();
        this.getFill();
        //System.out.println("trigger get table");
        return this.table;
    }
    
    public int getCol(){
        return this.col;
    }
    
    public int getRow(){
        return this.row;
    }
    
    public void tambahDataTest(double R, double G, double B){
        double[] baru = {R, G, B};
//        System.out.println(data_test);
        if(data_test == null){
            System.out.println("Dalam kondisi data_test null");
            double[][] test_baru = {baru};
            this.data_test = test_baru;
        } else {
            double[][] test_baru = new double[this.data_test.length+1][this.data_test[0].length];
            for(int i=0; i<test_baru.length; i++){
                for(int j=0; j<test_baru[0].length; j++){
                    if(i == test_baru.length-1){
                        test_baru[i][j] = baru[j];
                    } else {
                        test_baru[i][j] = this.data_test[i][j];
                    }
                }
            }
            this.data_test = test_baru;
        }
        
        for(double[] m : this.data_test) {
            for(double n: m){
                System.out.print(n + " ");
            }
            System.out.println("");
        }
        
    }
    
    public void hitungNaiveBayes(){
        double itungan_kiri = 1.0/Math.sqrt(2.0*Math.PI*this.varians[0][1]);
        double itungan_kanan = Math.pow(Math.E, Math.pow(data_test[0][1]+mean[0][1], 2)/(-2.0*varians[0][1]));
        String result = "";
        
        //Peluang SkinRGB
        double PSkinR = ((1.0/Math.sqrt(2.0*Math.PI*varians[0][0]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][0])+mean[0][0],2.0)/(-2.0*varians[0][0])))) ;
        double PSkinG = ((1.0/Math.sqrt(2.0*Math.PI*varians[0][1]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][1])+mean[0][1],2.0)/(-2.0*varians[0][1])))) ;
        double PSkinB = ((1.0/Math.sqrt(2.0*Math.PI*varians[0][2]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][2])+mean[0][2],2.0)/(-2.0*varians[0][2])))) ;
        
        //Peluang NoSkinRGB
        double PNoSkinR = ((1.0/Math.sqrt(2.0*Math.PI*varians[1][0]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][0])+mean[1][0],2.0)/(-2.0*varians[1][0])))) ;
        double PNoSkinG = ((1.0/Math.sqrt(2.0*Math.PI*varians[1][1]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][1])+mean[1][1],2.0)/(-2.0*varians[1][1])))) ;
        double PNoSkinB = ((1.0/Math.sqrt(2.0*Math.PI*varians[1][2]))*(Math.pow(Math.E,Math.pow((-1 * data_test[0][2])+mean[1][2],2.0)/(-2.0*varians[1][2])))) ;
        
        //Peluang Skin dan NoSkin
        double PSkin = total[0]/(total[0]+total[1]);
//        System.out.println("############################################333" + total[0]);   
//        System.out.println("############################################333" + totalData);   
        double PNoSkin = total[1]/(total[0]+total[1]);
        
        //Peluang Total
        double PTotSkin = PSkinR * PSkinG * PSkinB * PSkin;
        double PTotNoSkin = PNoSkinR * PNoSkinG * PNoSkinB * PNoSkin;
        
        System.out.println("cek data -----------");
        System.out.println(varians[0][0]);
        System.out.println(mean[0][0]);
        System.out.println(data_test[0][0]);
        System.out.println("cek data -----------");
        System.out.println(itungan_kiri);
        System.out.println(itungan_kanan);
        System.out.println(itungan_kiri * itungan_kanan);
        System.out.println("");
        
        System.out.println("Hasil NoSkin R");
        System.out.println(PNoSkinR);
        System.out.println("Hasil NoSkin G");
        System.out.println(PNoSkinG);
        System.out.println("Hasil NoSkin B");
        System.out.println(PNoSkinB);
        
        System.out.println("");
        System.out.println("Total Peluang NoSkin");
        System.out.println(PTotNoSkin);
        
        System.out.println("");
        System.out.println("Total Peluang Skin");
        System.out.println(PTotSkin);
        
        JFrame JF = new JFrame();
        
        if(PTotSkin > PTotNoSkin){
            result = "Skin";
        }else if (PTotSkin == PTotNoSkin){
            //Kurangi atau Tambah Data Training
            JOptionPane.showMessageDialog(JF,"Kurangi atau Tambah Data Training karena hasil peluang Skin dan NoSkin sama");
        }else{
            result = "No Skin";
        }
        
        
        JOptionPane.showMessageDialog(JF,"Peluang Skin = "+ PTotSkin + "\n" + "Peluang No Skin = " + PTotNoSkin +"\n" +
                                      "Maka Hasilnya adalah " + result);
        this.data_test = null;
        System.out.println("Masuk ke null");
    }
    
}
