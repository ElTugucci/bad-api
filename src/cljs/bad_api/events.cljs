(ns bad-api.events

  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require
   [clojure.string :as str]
   [bad-api.betterer :as btr] ;; contains useful functions and data
   [re-frame.core :as rf]
   [bad-api.db :as db]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<!]]
   ))


(defn into-availability-list [response]
  (doseq [r  response]
    (swap! btr/availability-list assoc (keyword (str/lower-case (:id r)))
           (btr/clean (:DATAPAYLOAD r))))
  (distinct @btr/availability-list)
  (rf/dispatch [:set-products @btr/availability-list "availability"]))


(defn get-availability [manufacturer-list]
  (doseq [m manufacturer-list]
    (go (let [response (<! (http/get (str btr/proxy
                                          "https://bad-api-assignment.reaktor.com/v2/availability/" m)
                                     {:with-credentials? false}))]
          (into-availability-list (:response (:body response)))
          #_(print (str m " " (:status response)))
          ))))


(defn get-manufacturer [item]
  (doseq [i item]
    (swap!  btr/manufacturer-list conj (:manufacturer i)))
  
  (reset! btr/manufacturer-list (distinct @btr/manufacturer-list)) ;; remove duplicates
  (print @btr/manufacturer-list)
  (rf/dispatch [:set-products @btr/manufacturer-list "manufacturers"])
  (get-availability @btr/manufacturer-list) )




(defn api-get-products [product-list]
   (doseq [product product-list]
    (go (let [response (<! (http/get (str btr/proxy
                                          "https://bad-api-assignment.reaktor.com/v2/products/" product)
                                     {:with-credentials? false}))]
          
          (print (str product " " (:status response)))
          (get-manufacturer (:body response))
          (rf/dispatch [:set-products  (:body response) product])
          ))))




(rf/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;; initial 
(rf/reg-event-fx
 :init-load
 (fn [_ [_ ]]
   (api-get-products btr/products-list)
   ))

;;update view
(rf/reg-event-db
 :select-view
 (fn [db [_ view]]
   (assoc db :view view) 
   ))

;; update the state 
(rf/reg-event-db
 :set-products
 (fn [db [_ product key]]
   (assoc db (keyword key) product)
   ))
