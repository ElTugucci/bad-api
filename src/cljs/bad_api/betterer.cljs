;;Miscallenious data and useful functions to make bad-api better

(ns bad-api.betterer
  (:require
   [clojure.string :as str]
   [re-frame.core :as rf]))

;; CORS error workaround
(def proxy "https://secure-anchorage-96745.herokuapp.com/") 

(def products-list ["beanies" "facemasks" "gloves"]) ;; this is given in the assignment
(def manufacturer-list (atom [])) ;; list of manufacturers we don't know, so we need to get it from :products api response, based on it we will make :availability api request

(def availability-list (atom {})) ;; here we will combine clean {:id availability}
;;pairs and set it it to app db (state)


(defn clean [item] ;; this function extracts availabilty from :DATAPAYLOAD
  (str/lower-case 
   (str/join 
    (re-seq #"[^<>/]" 
            (get
             (str/split
              item
              #"INSTOCKVALUE") 1)))))
