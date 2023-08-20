import org.apache.poi.xssf.usermodel.*;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSPGUI {

    public static void main(String[] args) {
        String inputFile = "C:/Users/marco/IdeaProjects/csp/src/car_sales.csv"; // Ruta del archivo de entrada CSV
        String outputFile = "sales_prefix_sum.xlsx"; // Nombre del archivo de salida Excel

        try {
            List<SaleRecord> sales = readSalesFromFile(inputFile); // Lee los registros de ventas desde el archivo CSV
            List<Double> prefixSums = calculatePrefixSums(sales); // Calcula las sumas prefijas de las ventas
            writePrefixSumsToExcel(outputFile, sales, prefixSums); // Escribe las sumas prefijas en un archivo Excel

            System.out.println("Tabla de sumas prefijas generada exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Definición de la clase interna para representar un registro de venta
    private static class SaleRecord {
        int id;
        double saleAmount;

        public SaleRecord(int id, double saleAmount) {
            this.id = id;
            this.saleAmount = saleAmount;
        }
    }

    // Lee los registros de ventas desde el archivo CSV y los almacena en una lista
    private static List<SaleRecord> readSalesFromFile(String filename) throws IOException {
        List<SaleRecord> sales = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true; // Para identificar la primera línea (encabezados)
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Saltar la primera línea (encabezados)
                }

                String[] values = line.split(",");
                if (values.length >= 5) {
                    int id = Integer.parseInt(values[0]); // Convierte el ID a un entero
                    double sale = parseMoneyValue(values[4]); // Convierte el valor monetario a un número decimal
                    sales.add(new SaleRecord(id, sale)); // Agrega un nuevo registro de venta a la lista
                }
            }
        }

        return sales;
    }

    // Convierte un valor monetario (como "$5257.07") en un número decimal
    private static double parseMoneyValue(String moneyValue) {
        String cleanValue = moneyValue.replace("$", "").replace(",", ""); // Elimina el símbolo de dólar y las comas
        return Double.parseDouble(cleanValue); // Convierte la cadena en un número decimal
    }

    // Calcula las sumas prefijas de las ventas
    private static List<Double> calculatePrefixSums(List<SaleRecord> sales) {
        List<Double> prefixSums = new ArrayList<>(sales.size());
        double prefixSum = 0;

        for (SaleRecord saleRecord : sales) {
            prefixSum += saleRecord.saleAmount; // Agrega la venta actual a la suma acumulativa
            prefixSums.add(prefixSum); // Agrega la suma acumulativa a la lista de sumas prefijas
        }

        return prefixSums;
    }

    // Escribe las sumas prefijas y los IDs de ventas en un archivo Excel
    private static void writePrefixSumsToExcel(String filename, List<SaleRecord> sales, List<Double> prefixSums) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(); // Crea un nuevo libro de trabajo Excel
        XSSFSheet sheet = workbook.createSheet("Prefix Sums"); // Crea una hoja de Excel con nombre "Prefix Sums"

        int rowNum = 0;
        for (int i = 0; i < sales.size(); i++) {
            XSSFRow row = sheet.createRow(rowNum++); // Crea una nueva fila en la hoja
            XSSFCell idCell = row.createCell(0); // Crea una celda para el ID de venta
            idCell.setCellValue(sales.get(i).id); // Asigna el valor del ID de venta a la celda

            XSSFCell sumCell = row.createCell(1); // Crea una celda para la suma prefija
            sumCell.setCellValue(prefixSums.get(i)); // Asigna la suma prefija a la celda
        }

        try (FileOutputStream outputStream = new FileOutputStream(filename)) {
            workbook.write(outputStream); // Escribe el libro de trabajo en el archivo de salida
        }

        workbook.close(); // Cierra el libro de trabajo
    }
}
