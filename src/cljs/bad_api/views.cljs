(ns bad-api.views
  (:require

   [bad-api.subs :as subs]
   [clojure.string :as str]
   [re-frame.core :as rf] 
   [bad-api.events]
   [bad-api.betterer :as btr]
   [reagent.core :as r]
   [reagent.dom :as d]
   ))

(defn display-availability [id]
  [:div.color
   (case ((keyword id) @(rf/subscribe [:availability]))
     "instock" [:div "In Stock"]
     "outofstock" [:div "Out of Stock"]
     "lessthan10" [:div "Less than 10"]
     [:div "🌍\n loading..."])])

(defn display-colors [color]
  [:div.color
   (for [c color]
     ^{:key c}
     [:div.color-blob {:style {:background-color c}}])])

(defn show-list-item [{:keys [id name type manufacturer color price]}]
  [:div.list-item
   [:div.list-unit type]
   [:div.list-unit (str "ᵇʸ " manufacturer )] 
   [:div.list-unit name]
   [:div.list-unit "colors" (display-colors color)]
   [:div.list-unit (str price "€")]
   [:div.list-unit (display-availability id)]] 
  )

(defn display-products [products]
  (when-not (empty? products)

    [:div.list
     (for [product (take 50 products)]
       ^{:key product}
       [:div  [show-list-item product]]
       )]))

(defn tab [title view id]
  [:div.tab
   {:class-name (when (= id @view) "active")}
   [:a
    {:href     "#"
     :on-click #(rf/dispatch [:select-view id])}
    title]])

(defn tab-bar [view]
  [:div.tab-bar
   [tab "beanies" view :beanies]
   [tab "facemasks" view :facemasks]
   [tab "gloves" view :gloves]
   ]
  )


(defn main-panel []
  (let [view (rf/subscribe [:view])]

    [:div
     [tab-bar view]
     
     (case @view
       :beanies (display-products @(rf/subscribe [:beanies]))
       :facemasks (display-products @(rf/subscribe [:facemasks]))
       :gloves (display-products @(rf/subscribe [:gloves]))
       )]))