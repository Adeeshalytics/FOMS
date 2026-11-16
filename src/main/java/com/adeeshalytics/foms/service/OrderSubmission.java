package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;

/** A replay is an existing order with identical submission fields. */
public record OrderSubmission(Order order, boolean replay) { }
