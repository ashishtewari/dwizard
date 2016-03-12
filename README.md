* Path for the CustomerResource class is "/v1/customer"
* If we give path for only customer class like "https://localhost:8443/v1/customer" it will give an exception as page 
  not found.
* In Header parameters specify your "API authentication key" as content,value pair like content="apikey" and
  value="your key". Your value should be in json format.
  			for example : content=apikey value= {   "apikey": "123456789" }
  			
* If you give wrong key your will get unauthorized exception or if you give content as other than "apikey" you will
  get "specify content correctly exception". 