(defproject sqlingvo "0.8.0"
  :description "A SQL DSL in Clojure."
  :url "http://github.com/r0man/sqlingvo"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :deploy-repositories [["releases" :clojars]]
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.postgresql/postgresql "9.4-1206-jdbc42"]]
  :profiles {:dev {:dependencies [[funcool/clojure.jdbc "0.6.1"]
                                  [org.clojure/java.jdbc "0.4.2"]]
                   :plugins [[jonase/eastwood "0.2.2"]
                             [lein-difftest "2.0.0"]]}}
  :aliases {"lint" ["do" ["eastwood"]]
            "ci" ["do" ["difftest"] ["lint"]]}
  :eastwood {:exclude-linters [:suspicious-expression]})
