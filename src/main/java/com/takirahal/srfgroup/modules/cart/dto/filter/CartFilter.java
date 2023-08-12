package com.takirahal.srfgroup.modules.cart.dto.filter;

import com.takirahal.srfgroup.modules.cart.enums.StatusCart;
import com.takirahal.srfgroup.modules.user.dto.filter.UserOfferFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartFilter {
    private Long id;
    private StatusCart status;
    UserOfferFilter user;
}
