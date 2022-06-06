package com.nurkiewicz.reactor.user;

import com.google.common.collect.ImmutableList;
import reactor.core.publisher.Mono;

public class UserOrders {

	public static Mono<Order> lastOrderOf(User user) {
		return Mono.fromCallable(() -> {
			if (user.getId() > 10) {
				return new Order(ImmutableList.of(
						new Item("Item of A" + user.getId()),
						new Item("Item of B" + user.getId()))
				);
			} else {
				return null;
			}
		});
	}

}
