package org.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CrptApi {

    public static void main(String[] args) {
        String apiUrl = "https://ismp.crpt.ru/api/v3/lk/documents/create";
        CrptApi crptApi = new CrptApi(apiUrl);


        Description description = new Description("string");

        Product product1 = new Product("c1", "2020-01-27",
                "123", "o1", "p1", "2020-01-23",
                "tnved1", "uit1", "uitu1");
        Product product2 = new Product("c2", "2020-01-23",
                "456", "o2", "p2", "2020-01-23",
                "tnved2", "uit2", "uitu2");

        Document document = new Document(description, "string", "string", true, "LP_INTRODUCE_GOODS",
                "string", "string", "string", "2020-01-23",
                "string", List.of(product1, product2), "2020-01-23", "r123");
        String signature = "<Открепленная подпись в base64>";
        crptApi.createDocument(document, signature);
    }

    private final String apiUrl;
    private final Logger logger = Logger.getLogger("crptApiLog");


    public void createDocument(Object document, String signature) {

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(apiUrl);

            httpPost.setHeader("Content-Type", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            String documentJson = objectMapper.writeValueAsString(document);

            String requestBody = String.format("{ \"product_document\": \"%s\", \"document_format\": \"MANUAL\"," +
                    " \"type\": \"LP_INTRODUCE_GOODS\", \"signature\": \"%s\" }", documentJson, signature);
            StringEntity entity = new StringEntity(requestBody);
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    logger.info("Document was created!");
                } else {
                    logger.warning("Document creation error, status code: " + statusCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

@AllArgsConstructor
@Getter
@Setter
class Document {
    private Description description;
    @JsonProperty("doc_id")
    private String docId;
    @JsonProperty("doc_status")
    private String docStatus;
    private boolean importRequest;
    @JsonProperty("doc_type")
    private String docType;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("participant_inn")
    private String participantInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    private String productionDate;
    @JsonProperty("production_type")
    private String productionType;
    private List<Product> productsList;
    @JsonProperty("reg_date")
    private String regDate;
    @JsonProperty("reg_number")
    private String regNumber;

}

@AllArgsConstructor
@Getter
@Setter
class Description {
    private String participantInn;
}

@AllArgsConstructor
@Getter
@Setter
class Product {
    @JsonProperty("certificate_document")
    private String certificateDocument;
    @JsonProperty("certificate_document_date")
    private String certificateDocumentDate;
    @JsonProperty("certificate_document_number")
    private String certificateDocumentNumber;
    @JsonProperty("owner_inn")
    private String ownerInn;
    @JsonProperty("producer_inn")
    private String producerInn;
    @JsonProperty("production_date")
    private String productionDate;
    @JsonProperty("tnved_code")
    private String tnvedCode;
    @JsonProperty("uit_code")
    private String uitCode;
    @JsonProperty("uitu_code")
    private String uituCode;
}