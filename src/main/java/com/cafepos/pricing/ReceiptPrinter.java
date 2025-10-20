package com.cafepos.pricing;

public final class ReceiptPrinter {
    public String format(String recipe, int qty, PricingService.PricingResult pr, FixedRateTaxPolicy taxPolicy) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(pr.subtotal()).append("\n");
        if (pr.discount().asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(pr.discount()).append("\n");
        }
        receipt.append("Tax (")
                .append(taxPolicy.percent()) //use getter for more consistency
                .append("%): ")
                .append(pr.tax())
                .append("\n");
        receipt.append("Total: ").append(pr.total());
        return receipt.toString();
    }
}
