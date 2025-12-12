
package ltts.com.securityconfig;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import ltts.com.model.Order;
import ltts.com.model.OrderItem;
import ltts.com.model.Product;
import ltts.com.repository.OrdersRepo;

@Service
public class OrderPdfService {

    @Autowired
    private OrdersRepo ordersRepo;

    public byte[] generatePdfForOrder(Long orderId) throws Exception {

        Order order = ordersRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        String html = buildXhtml(order);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "");     // baseUri required even if empty
            builder.toStream(out);

            

            builder.run();
            return out.toByteArray();
        }
    }
    private String getImageTag(OrderItem item) {
        try {
            if (item.getProduct() == null) return placeholderImage();

            Product p = item.getProduct();
            if (p.getImages() == null || p.getImages().isEmpty()) return placeholderImage();

            byte[] compressed = p.getImages().get(0);
            byte[] decompressed = ImageUtils.decompressImage(compressed);

            String mime = "image/png"; // fallback
            if (p.getImageContentTypes() != null && !p.getImageContentTypes().isEmpty())
                mime = p.getImageContentTypes().get(0);

            String base64 = Base64.getEncoder()
                    .encodeToString(decompressed)
                    .replace("\n", "")
                    .replace("\r", "");

            return "<img class=\"item-image\" src=\"data:" + mime + ";base64," + base64 + "\" alt=\"product\" />";
        } catch (Exception e) {
            return placeholderImage();
        }
    }

//    private String buildXhtml(Order order) {
//
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a");
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
//        sb.append("<head>");
//        sb.append("<meta charset=\"UTF-8\" />");
//
//        sb.append("<style>");
//
//        // GLOBAL
//        sb.append("body { font-family: Arial, sans-serif; font-size: 13px; color: #333; padding: 25px; }");
//        sb.append("h2 { margin: 0; padding: 0; font-size: 22px; color: #2d3748; }");
//        sb.append(".section-title { font-weight: bold; font-size: 15px; margin-bottom: 6px; }");
//
//        // HEADER
//        sb.append(".invoice-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 2px solid #4a5568; padding-bottom: 12px; }");
//        sb.append(".brand { font-size: 26px; font-weight: bold; color: #2b6cb0; }");
//        sb.append(".invoice-meta { text-align: right; font-size: 13px; color:#4a5568; }");
//
//        // CUSTOMER INFO
//        sb.append(".customer-box { padding: 12px; border: 1px solid #cbd5e0; background: #f7fafc; border-radius: 6px; margin-bottom: 20px; }");
//
//        // TABLE
//        sb.append("table { width:100%; border-collapse: collapse; margin-top: 15px; }");
//        sb.append("th { background: #edf2f7; padding: 10px; border: 1px solid #cbd5e0; font-size: 13px; }");
//        sb.append("td { padding: 10px; border: 1px solid #e2e8f0; font-size: 13px; }");
//        sb.append("tr:nth-child(even) { background: #f7fafc; }");
//
//        // IMAGE
//        sb.append(".item-image { width: 70px; height: 70px; object-fit: contain; border: 1px solid #e2e8f0; border-radius: 4px; }");
//
//        // TOTAL BOX
//        sb.append(".total-box { margin-top: 20px; width: 250px; float: right; border: 1px solid #cbd5e0; padding: 10px; border-radius: 6px; background:#f7fafc; }");
//        sb.append(".total-row { display:flex; justify-content: space-between; padding:6px 0; font-size:14px; }");
//        sb.append(".total-row strong { font-size: 15px; }");
//
//        // FOOTER
//        sb.append(".footer { margin-top: 60px; text-align:center; font-size: 11px; color:#718096; }");
//
//        sb.append("</style>");
//        sb.append("</head>");
//        sb.append("<body>");
//
//        // ========================= HEADER =========================
//        sb.append("<div class='invoice-header'>");
//        sb.append("<div class='brand'>TasteTreasures</div>");
//        sb.append("<div class='invoice-meta'>");
//        sb.append("Order ID: <strong>").append(order.getId()).append("</strong><br/>");
//        sb.append("Placed On: ").append(order.getCreatedAt() != null ? dtf.format(order.getCreatedAt()) : "").append("<br/>");
//        sb.append("Order Status: <strong>").append(order.getStatus().name()).append("</strong>");
//        sb.append("</div>");
//        sb.append("</div>");
//
//        // ====================== CUSTOMER ======================
//        sb.append("<div class='section-title'>Customer Details</div>");
//        sb.append("<div class='customer-box'>");
//
//        sb.append("<div><strong>Name:</strong> ").append(escape(order.getBuyer().getName())).append("</div>");
//        sb.append("<div><strong>Email:</strong> ").append(escape(order.getBuyer().getEmail())).append("</div>");
//        sb.append("<div><strong>Phone:</strong> ").append(escape(order.getBuyer().getPhone())).append("</div>");
//        sb.append("<div><strong>Address:</strong> ").append(escape(order.getOrderAddress())).append("</div>");
//
//        sb.append("</div>");
//
//        // ====================== ITEMS TABLE ======================
//        sb.append("<div class='section-title'>Order Items</div>");
//        sb.append("<table>");
//        sb.append("<tr>")
//                .append("<th>Image</th>")
//                .append("<th>Product</th>")
//                .append("<th>Variant</th>")
//                .append("<th>Price</th>")
//                .append("<th>Qty</th>")
//                .append("<th>Total</th>")
//                .append("</tr>");
//
//        double grandTotal = 0;
//
//        for (OrderItem item : order.getItems()) {
//        	
//            double line = (item.getPrice() * item.getQuantity());
//            grandTotal += line;
//
//            String imgTag = getImageTag(item);
//
//            sb.append("<tr>")
//                    .append("<td>").append(imgTag).append("</td>")
//                    .append("<td>").append(escape(item.getProduct().getName())).append("</td>")
//                    .append("<td>").append(escape(item.getVariant())).append("</td>")
//                    .append("<td>").append("Rs. ").append(item.getPrice()).append("</td>")
//                    .append("<td>").append(item.getQuantity()).append("</td>")
//                    .append("<td>").append("Rs. ").append(line).append("</td>")
//                    .append("</tr>");
//        	        }
//        	//
//
//        sb.append("</table>");
//
//        // ====================== TOTAL BOX ======================
//        sb.append("<div class='total-box'>");
//        
//
//        sb.append("<div class='total-row'><strong>Total:</strong><strong>Rs. ")
//                .append(String.format("%.2f", grandTotal)).append("</strong></div>");
//
//        sb.append("</div>");
//
//        // ====================== FOOTER ======================
//        sb.append("<div class='footer'>");
//        sb.append("Thank you for ordering from TasteTreasures! For support contact: support@tastetreasures.in");
//        sb.append("</div>");
//
//        sb.append("</body></html>");
//
//        return sb.toString();
//    }

    private String buildXhtml(Order order) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy | hh:mm a");

        StringBuilder sb = new StringBuilder();

        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        sb.append("<head>");
        sb.append("<meta charset=\"UTF-8\" />");

        sb.append("<style>");

        /* GLOBAL */
        sb.append("body { font-family: Arial, sans-serif; font-size: 13px; color: #333; padding: 25px; }");
        sb.append("h2 { margin:0; padding:0; font-size: 22px; color: #2d3748; }");
        sb.append(".section-title { font-weight: bold; font-size: 15px; margin-top: 20px; margin-bottom: 6px; }");

        /* HEADER – using table instead of flex */
        sb.append(".header-table { width:100%; border-bottom:2px solid #4a5568; margin-bottom:20px; }");
        sb.append(".brand { font-size:26px; font-weight:bold; color:#2b6cb0; }");
        sb.append(".invoice-meta { text-align:right; font-size:13px; color:#4a5568; }");

        /* CUSTOMER BOX */
        sb.append(".customer-box { padding: 12px; border:1px solid #cbd5e0; background:#f7fafc; border-radius:6px; }");

        /* TABLE */
        sb.append("table { width:100%; border-collapse:collapse; margin-top:10px; }");
        sb.append("th { background:#edf2f7; padding:8px; border:1px solid #cbd5e0; font-size:13px; }");
        sb.append("td { padding:8px; border:1px solid #e2e8f0; font-size:13px; }");
        sb.append("tr:nth-child(even) { background:#f7fafc; }");

        /* IMAGE */
        sb.append(".item-image { width:70px; height:70px; border:1px solid #ccc; }"); 
        // removed object-fit

        /* TOTAL BOX */
        sb.append(".total-box { width:260px; border:1px solid #cbd5e0; padding:10px; background:#f7fafc; margin-top:20px; float:right; }");
        sb.append(".total-row { width:100%; font-size:14px; padding:5px 0; }");
        sb.append(".left { float:left; }");
        sb.append(".right { float:right; font-weight:bold; }");

        /* FOOTER */
        sb.append(".footer { margin-top:50px; text-align:center; font-size:11px; color:#718096; }");

        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");

        // HEADER TABLE
        sb.append("<table class='header-table'>");
        sb.append("<tr>");
        sb.append("<td class='brand'>TasteTreasures</td>");
        sb.append("<td class='invoice-meta'>")
                .append("Order ID: <strong>").append(order.getId()).append("</strong><br/>")
                .append("Placed On: ").append(order.getCreatedAt() != null ? dtf.format(order.getCreatedAt()) : "").append("<br/>")
                .append("Order Status: <strong>").append(order.getStatus().name()).append("</strong>")
                .append("</td>");
        sb.append("</tr>");
        sb.append("</table>");

        // CUSTOMER SECTION
        sb.append("<div class='section-title'>Customer Details</div>");
        sb.append("<div class='customer-box'>");

        sb.append("<div><strong>Name:</strong> ").append(escape(order.getBuyer().getName())).append("</div>");
        sb.append("<div><strong>Email:</strong> ").append(escape(order.getBuyer().getEmail())).append("</div>");
        sb.append("<div><strong>Phone:</strong> ").append(escape(order.getBuyer().getPhone())).append("</div>");
        sb.append("<div><strong>Address:</strong> ").append(escape(order.getOrderAddress())).append("</div>");

        sb.append("</div>");

        // ITEMS
        sb.append("<div class='section-title'>Order Items</div>");
        sb.append("<table>");
        sb.append("<tr>")
                .append("<th>Image</th>")
                .append("<th>Product</th>")
                .append("<th>Variant</th>")
                .append("<th>Price</th>")
                .append("<th>Qty</th>")
                .append("<th>Total</th>")
                .append("</tr>");

        double grandTotal = 0;
        for (OrderItem item : order.getItems()) {
            double line = item.getPrice() * item.getQuantity();
            grandTotal += line;

            String imgTag = getImageTag(item);

            sb.append("<tr>")
                    .append("<td>").append(imgTag).append("</td>")
                    .append("<td>").append(escape(item.getProduct().getName())).append("</td>")
                    .append("<td>").append(escape(item.getVariant())).append("</td>")
                    .append("<td>Rs. ").append(item.getPrice()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>Rs. ").append(line).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");

        // TOTAL BOX
        sb.append("<div class='total-box'>");
        sb.append("<div class='total-row'><span class='left'>Total:</span><span class='right'>Rs. ")
                .append(String.format("%.2f", grandTotal)).append("</span></div>");
        sb.append("</div>");

        // FOOTER
        sb.append("<div class='footer'>Thank you for ordering from TasteTreasures! Email: support@tastetreasures.in</div>");

        sb.append("</body></html>");

        return sb.toString();
    }

    private String placeholderImage() {
        return "<div style=\"width:70px;height:70px;background:#eee;color:#888;"
                + "display:flex;align-items:center;justify-content:center;font-size:10px;\">No Img</div>";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
