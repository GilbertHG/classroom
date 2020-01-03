package com.exomatik.classroom.classroom.Dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.mtp.MtpConstants;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.DetailKelas;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelPenilaian;
import com.exomatik.classroom.classroom.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogExcel extends DialogFragment {
    public static TableLayout tableLayout;
    public static ModelKelas dataKelas;
    private Button customDialog_Dismiss, buttonTambah;
    private EditText etNama;
    private String path;

    public static CustomDialogExcel newInstance() {
        return new CustomDialogExcel();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_excel, container, false);

        customDialog_Dismiss = (Button) dialogView.findViewById(R.id.dialog_dismiss);
        buttonTambah = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etNama = (EditText) dialogView.findViewById(R.id.et_tambah);

        buttonTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaFile = etNama.getText().toString();

                if (namaFile.isEmpty()){
                    etNama.setError("Tidak boleh kosong");
                }
                else {
                    if (saveExcelFile(getContext(), namaFile + ".xls")) {
                        open_file(path);
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Gagal Membuat File", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        customDialog_Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout = null;
                dataKelas = null;
                dismiss();
            }
        });

        return dialogView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean saveExcelFile(Context context, String fileName) {
        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setFillForegroundColor(HSSFColor.WHITE.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Kelas " + dataKelas.getNamaKelas());

        int indexAkhir = 1;
        int indexAwal = 1;
        for (int title = 0; title < dataKelas.getModelNilaiKelas().size(); title++) {
            if (dataKelas.getModelNilaiKelas().get(title).isNamaPelajar()) {
                indexAwal++;
                indexAkhir++;
            } else if (dataKelas.getModelNilaiKelas().get(title).isKehadiran()) {
                indexAkhir = indexAkhir + dataKelas.getModelNilaiKelas().get(title).getJumlah() - 1;
                sheet1.addMergedRegion(new CellRangeAddress(0, 0, indexAwal, indexAkhir));
                indexAkhir++;
                indexAwal = indexAkhir;
            } else if (dataKelas.getModelNilaiKelas().get(title).getJumlah() > 1) {
                indexAkhir = indexAkhir + dataKelas.getModelNilaiKelas().get(title).getJumlah() - 1;
                sheet1.addMergedRegion(new CellRangeAddress(0, 0, indexAwal, indexAkhir));
                indexAkhir = indexAkhir++;
                indexAwal = indexAkhir;
            }
        }

        for (int indexBawah = 0; indexBawah < tableLayout.getChildCount(); indexBawah++) {
            View view = tableLayout.getChildAt(indexBawah);
            TableRow rowData = (TableRow) view;

            Row row = sheet1.createRow(indexBawah);

            if (indexBawah == 0) {
                c = row.createCell(0);
                c.setCellValue("No. ");
                c.setCellStyle(cs);

                int index = 1;
                for (int title = 0; title < dataKelas.getModelNilaiKelas().size(); title++) {
                    if (dataKelas.getModelNilaiKelas().get(title).isNamaPelajar()) {
                        c = row.createCell(index);
                        c.setCellValue(dataKelas.getModelNilaiKelas().get(title).getNama());
                        c.setCellStyle(cs);
                        index++;
                    } else if (dataKelas.getModelNilaiKelas().get(title).isKehadiran()) {
                        c = row.createCell(index);
                        c.setCellValue(dataKelas.getModelNilaiKelas().get(title).getNama() + "(" + Integer.toString(dataKelas.getModelNilaiKelas().get(title).getPersentase()) + "%)");
                        c.setCellStyle(cs);
                        index = index + dataKelas.getModelNilaiKelas().get(title).getJumlah();
                    } else if (dataKelas.getModelNilaiKelas().get(title).getJumlah() > 1) {
                        c = row.createCell(index);
                        c.setCellValue(dataKelas.getModelNilaiKelas().get(title).getNama() + "(" + Integer.toString(dataKelas.getModelNilaiKelas().get(title).getPersentase()) + "%)");
                        c.setCellStyle(cs);
                        index = index + dataKelas.getModelNilaiKelas().get(title).getJumlah();
                    } else {
                        c = row.createCell(index);
                        c.setCellValue(dataKelas.getModelNilaiKelas().get(title).getNama() + "(" + Integer.toString(dataKelas.getModelNilaiKelas().get(title).getPersentase()) + "%)");
                        c.setCellStyle(cs);
                        index++;
                    }
                }

                for (int indexSamping = dataKelas.getModelNilaiKelas().size() + 1; indexSamping < rowData.getChildCount(); indexSamping++) {
                    TextView textView = (TextView) rowData.getChildAt(indexSamping);
                    c = row.createCell(index);
                    c.setCellValue(textView.getText().toString());
                    c.setCellStyle(cs);
                    index++;
                }
            } else {
                for (int indexSamping = 0; indexSamping < rowData.getChildCount(); indexSamping++) {
                    TextView textView = (TextView) rowData.getChildAt(indexSamping);

                    c = row.createCell(indexSamping);
                    c.setCellValue(textView.getText().toString());
                    c.setCellStyle(cs);
                }
            }
        }

        // Create a path where we will place our List of objects on external storage
//        File file = new File(context.getExternalFilesDir(null), fileName);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator
                + "Download", fileName);
        FileOutputStream os = null;

        try

        {
            Log.w("Lokasi", file.getAbsolutePath().toString());
            os = new FileOutputStream(file.getAbsolutePath().toString());
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            path = file.getAbsolutePath().toString();
        } catch (
                IOException e)

        {
            Toast.makeText(context, "Error writing, Please make sure u already have folder Download in Phone", Toast.LENGTH_LONG).show();
        } catch (
                Exception e)

        {
            Toast.makeText(context, "Failed, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        } finally

        {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void open_file(String filename) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()
                + File.separator + "Downloads" + File.separator);

        Log.e("File", uri.toString());
        Toast.makeText(getActivity(), "File : " + filename, Toast.LENGTH_LONG).show();
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }

}