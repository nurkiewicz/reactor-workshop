package com.nurkiewicz.reactor.email;

import reactor.core.Disposable;

import java.util.function.Consumer;

public interface EmailClient extends Disposable  {

	void onEmail(Consumer<String> msg);

}
