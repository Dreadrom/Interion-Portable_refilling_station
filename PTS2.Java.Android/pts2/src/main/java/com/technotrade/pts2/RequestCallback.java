package com.technotrade.pts2;

import com.technotrade.pts2.network.IRequest;

/// <summary>
/// Callback interface
/// </summary>
@FunctionalInterface
public interface RequestCallback<T> {
	void execute(IRequest<T> request, T result);
}