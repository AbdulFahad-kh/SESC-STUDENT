package com.acme.studentportal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Service
public class FinanceService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${app.finance-base-url:http://localhost:8081}")
    private String financeBaseUrl = System.getenv().getOrDefault("FINANCE_BASE_URL", "http://localhost:8081");

    public void createStudentAccount(String studentId) {
        restTemplate.postForObject(financeBaseUrl + "/accounts", new StudentIdRequest(studentId), Void.class);
    }

    public void createInvoice(String studentId, double amount, String type, String dueDate) {
        Invoice invoice = new Invoice();
        invoice.account = new Account(studentId);
        invoice.amount = amount;
        invoice.type = type;
        invoice.dueDate = dueDate;
        restTemplate.postForObject(financeBaseUrl + "/invoices", invoice, Void.class);
    }

    public boolean isEligibleForGraduation(String studentId) {
        Account account = restTemplate.getForObject(financeBaseUrl + "/accounts/student/" + studentId, Account.class);
        return account != null && !account.hasOutstandingBalance;
    }

    public List<Invoice> getInvoicesByType(String studentId, String type) {
        Invoice[] all = restTemplate.getForObject(financeBaseUrl + "/invoices", Invoice[].class);
        if (all == null) return List.of();
        return java.util.Arrays.stream(all)
            .filter(inv -> inv.account != null && studentId.equals(inv.account.studentId) && type.equals(inv.type))
            .toList();
    }

    public List<Invoice> getOutstandingInvoices(String studentId) {
        try {
            org.springframework.http.ResponseEntity<String> resp = restTemplate.getForEntity(financeBaseUrl + "/invoices", String.class);
            String body = resp.getBody();
            if (body == null || body.isEmpty()) return List.of();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(body);
            com.fasterxml.jackson.databind.JsonNode invoiceListNode = root.path("_embedded").path("invoiceList");
            if (invoiceListNode.isMissingNode() || !invoiceListNode.isArray()) return List.of();
            List<Invoice> result = new java.util.ArrayList<>();
            System.out.println("[DEBUG] Searching for studentId: " + studentId);
            for (com.fasterxml.jackson.databind.JsonNode invNode : invoiceListNode) {
                String invStudentId = invNode.path("studentId").asText();
                String status = invNode.path("status").asText();
                System.out.println("[DEBUG] Found invoice for studentId: " + invStudentId + ", status: " + status);
                if (studentId != null && studentId.equalsIgnoreCase(invStudentId) && "OUTSTANDING".equalsIgnoreCase(status)) {
                    Invoice inv = mapper.treeToValue(invNode, Invoice.class);
                    result.add(inv);
                }
            }
            System.out.println("[DEBUG] Filtered outstanding invoices: " + result.size());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    static class StudentIdRequest {
        public String studentId;
        public StudentIdRequest(String studentId) { this.studentId = studentId; }
    }

    static class Account {
        public String studentId;
        public boolean hasOutstandingBalance;
        public Account() {}
        public Account(String studentId) { this.studentId = studentId; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Invoice {
        public Long id;
        public String reference;
        public Double amount;
        public String dueDate;
        public String type;
        public String status;
        public String studentId;
        public Account account;
    }
} 