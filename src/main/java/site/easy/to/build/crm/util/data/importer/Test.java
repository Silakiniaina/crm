package site.easy.to.build.crm.util.data.importer;

import com.google.gson.Gson;

public class Test {
    public static void main(String[] args) {
        CustomerDataImport cust = new CustomerDataImport();
        cust.setEmail("sanda@gmail.com");
        cust.setName("32");

        System.out.println(cust.toString());
    }
}
