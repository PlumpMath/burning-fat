(ns burning-fat.handler
  (:require [burning-fat.dev :refer [browser-repl start-figwheel]]
            [compojure.core :refer [GET defroutes]]
            [compojure.route :refer [not-found resources]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [selmer.parser :refer [render-file]]
            [environ.core :refer [env]]
            [prone.middleware :refer [wrap-exceptions]]
            [org.httpkit.server :refer [run-server]]
            [clojure.tools.logging :as log]
            [ring.middleware.logger :as rlogger])
  (:gen-class))

(defroutes routes
  (GET "/" [] (render-file "templates/index.html" {:dev (env :dev?)}))
  (resources "/")
  (not-found "Not Found"))

(def app
  (let [handler (wrap-defaults routes site-defaults)]
    (rlogger/wrap-with-logger
      (if (env :dev?)
        (wrap-exceptions handler)
        handler))))

(defn -main []
  (let [port (Integer. (or (env :port) "8080"))]
    (run-server app {:port port})
    (log/info "server running on port" port)))
