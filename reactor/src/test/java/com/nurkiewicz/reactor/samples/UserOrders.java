package com.nurkiewicz.reactor.samples;

import com.google.common.collect.ImmutableList;
import reactor.core.publisher.Mono;

public class UserOrders {

	public static Mono<Order> lastOrderOf(User user) {
		return Mono.fromCallable(() -> {
			if (user.getId() > 10) {
				return new Order(ImmutableList.of(new Item("Item of " + user.getId())));
			} else {
				return null;
			}
		});
	}

}
