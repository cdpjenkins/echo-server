(ns echo-server.core
  (:use clojure.contrib.server-socket))

(import
 '(java.io InputStreamReader BufferedReader PrintWriter
           BufferedOutputStream DataOutputStream
           BufferedInputStream DataInputStream)
 '(java.net InetAddress ServerSocket Socket))

(defn echo-protocol [in out]
  (let [in (->> in InputStreamReader. BufferedReader.)
        out (->> out PrintWriter.)]
    (loop []
      (let [line (.readLine in)]
        (condp = line
          "quit" nil
          
          ;; HACK if the client says kill-server then I want the
          ;; server shut down... there must be a less hacky way to
          ;; do it :-)
          "kill-server" (close-server server)

          (do 
            (.println out line)
            (.flush out)
            (recur)))))))

(defn make-server []
  (println "skank-ston")
  (create-server 1234 echo-protocol))

(defn echo-client [in out]
  (let [in (->> in InputStreamReader. BufferedReader.)
        out (->> out PrintWriter.)]
    (.println out "Hello!")
    (.flush out)
    (println "server returned: " (.readLine in))
    (.println out "quit")))

(defn to-hex-str [i]
  (str "0x" (Integer/toString (int  i) 16)))

(defn to-hex-vec [s]
  (apply str (interpose " " (map to-hex-str s))))

(defn echo-binary-client [in out]
  (let [ in  (->> in BufferedInputStream. DataInputStream.)
        out (->> out BufferedOutputStream. DataOutputStream. )
        out-bytes (byte-array (map byte [0x48 0x65 0x6c 0x6c 0x6f 0x20 0x64 0x61 0x74 0x61 0x20 0x77 0x6f 0x72 0x6c 0x64 0x21 0xa 0x71 0x75 0x69 0x74 0xa]))
        in-bytes (byte-array 1024)]
    (.write out out-bytes 0 (count out-bytes) )
    (.flush out)
    (loop []
      (let [bytes-read (.read in in-bytes)]
        (when (not= -1 bytes-read)
          (do
            (println (String. in-bytes 0 bytes-read))
            (recur)))))))

(defn connect-client [f]
  (let [cs (Socket. "localhost" 1234)
        in  (.getInputStream cs)
        out (.getOutputStream cs)]
    (println "about to call echo-client")
    (f in out)
    (.close cs)))
