(ns bad-api.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [bad-api.events :as events]
   [bad-api.views :as views]
   [bad-api.config :as config]
   ))



(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el))) 

(defn refresh-data [] ;; get data from server every 5 minutes
  (js/setInterval #(re-frame/dispatch [:init-load]) 300000))


(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (re-frame/dispatch-sync [:init-load])
  (refresh-data)
  (dev-setup)
  (mount-root))
