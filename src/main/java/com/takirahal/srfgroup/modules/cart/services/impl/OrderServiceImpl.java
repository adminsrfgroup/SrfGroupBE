package com.takirahal.srfgroup.modules.cart.services.impl;

import com.takirahal.srfgroup.modules.cart.dto.CartDTO;
import com.takirahal.srfgroup.modules.cart.dto.OrderDTO;
import com.takirahal.srfgroup.modules.cart.dto.filter.CartFilter;
import com.takirahal.srfgroup.modules.cart.dto.filter.OrderFilter;
import com.takirahal.srfgroup.modules.cart.entities.Cart;
import com.takirahal.srfgroup.modules.cart.entities.Order;
import com.takirahal.srfgroup.modules.cart.enums.StatusCart;
import com.takirahal.srfgroup.modules.cart.enums.StatusOrder;
import com.takirahal.srfgroup.modules.cart.mapper.OrderMapper;
import com.takirahal.srfgroup.modules.cart.models.DetailsCartGlobal;
import com.takirahal.srfgroup.modules.cart.models.DetailsCarts;
import com.takirahal.srfgroup.modules.cart.repositories.OrderRepository;
import com.takirahal.srfgroup.modules.cart.services.CartService;
import com.takirahal.srfgroup.modules.cart.services.OrderService;
import com.takirahal.srfgroup.modules.user.dto.filter.UserOfferFilter;
import com.takirahal.srfgroup.modules.user.exceptioins.AccountResourceException;
import com.takirahal.srfgroup.modules.user.mapper.UserMapper;
import com.takirahal.srfgroup.security.UserPrincipal;
import com.takirahal.srfgroup.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CartService cartService;

    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional
    public OrderDTO save(UserPrincipal userPrincipal, OrderDTO newOrderDTO) {
        Pageable pageable = PageRequest.of(0, 100);
        CartFilter cartFilter = new CartFilter();
        Page<CartDTO> listCarts = cartService.getCartsByCurrentUser(cartFilter, pageable);
        DetailsCarts detailsCarts = cartService.getDetailsCartsByPage(listCarts);

        Double somme = 0D;
        for (DetailsCartGlobal detailsCartGlobal : detailsCarts.getDetailsCartGlobal()) {
            somme = somme + detailsCartGlobal.getTotalCarts();
        }

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setNumberOfProducts(detailsCarts.getDetailsCartGlobal().size());
        // orderDTO.setTaxDelivery(detailsCarts.getTaxDelivery());
        orderDTO.setTotalCarts(somme);
        // orderDTO.setTotalGlobalCarts(detailsCarts.getTotalGlobalCarts());
        orderDTO.setPassedDate(Instant.now());
        orderDTO.setPaymentMode(newOrderDTO.getPaymentMode());
        orderDTO.setStatus(StatusOrder.PASSED.toString());
        orderDTO.setUser(userMapper.toCurrentUserPrincipal(userPrincipal));

        Set<CartDTO> hSet = new HashSet<>();
        for (CartDTO cartDTO : listCarts.getContent())
            hSet.add(cartDTO);
        orderDTO.setCarts(hSet);

        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);

        cartService.updateListCartByStatus(listCarts.getContent());
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDTO> getOrdersPassedByCurrentUser(OrderFilter orderFilter, Pageable pageable) {
        log.debug("Request to get all passed Orders for current user : {}", orderFilter);
        Long useId = SecurityUtils.getIdByCurrentUser();
        UserOfferFilter userOfferFilter = new UserOfferFilter();
        userOfferFilter.setId(useId);
        orderFilter.setStatus(StatusOrder.PASSED.toString());
        orderFilter.setUser(userOfferFilter);
        return findByCriteria(orderFilter, pageable);
    }


    @Override
    public Page<OrderDTO> getOrdersReceivedByCurrentUser(OrderFilter orderFilter, Pageable pageable) {
        log.debug("Request to get all received Orders for current user : {}", orderFilter);
        List<OrderDTO> orderDTOList = orderRepository.getReceivedOrders(pageable).stream().map(orderMapper::toDtoWithoutCarts).collect(Collectors.toList());;
        Page<OrderDTO> orderDTOS = new PageImpl<>(
                orderDTOList,
                pageable,
                orderDTOList.size()
        );
        return orderDTOS;
    }

    private Page<OrderDTO> findByCriteria(OrderFilter orderFilter, Pageable page) {
        log.debug("find carts by criteria : {}, page: {}", page);
        Page<Order> orders = orderRepository.findAll(createSpecification(orderFilter), page);
        return orders.map(orderMapper::toDto);
    }

    protected Specification<Order> createSpecification(OrderFilter orderFilter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if( orderFilter.getStatus() != null){
                predicates.add(criteriaBuilder.equal(root.get("status"), orderFilter.getStatus()));
            }

            if ( orderFilter.getUser() != null && orderFilter.getUser().getId() != null ) {
                predicates.add(criteriaBuilder.equal(root.get("user").get("id"), orderFilter.getUser().getId()));
            }

            query.orderBy(criteriaBuilder.desc(root.get("id")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
