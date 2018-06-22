package com.mobileenerlytics.controller;

class NotFoundEntityException extends RuntimeException{

    public NotFoundEntityException(Class entity, Object object) {
        super("could not find " + entity.getName() + " of " + object.toString());
    }
}
