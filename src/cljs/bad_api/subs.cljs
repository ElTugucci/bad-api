(ns bad-api.subs
  (:require
   [re-frame.core :as rf]))

(rf/reg-sub
 :name
 (fn [db _]
   (:name db)))

(rf/reg-sub
 :manufacturers
 (fn [db _]
   (:manufacturers db)))


(rf/reg-sub
 :beanies
 (fn [db _]
   (:beanies db)))


(rf/reg-sub
 :facemasks
 (fn [db _]
   (:facemasks db)))

(rf/reg-sub
 :gloves
 (fn [db _]
   (:gloves db)))

(rf/reg-sub
 :view
 (fn [db _]
   (:view db)))

(rf/reg-sub
 :availability
 (fn [db _]
   (:availability db)))
