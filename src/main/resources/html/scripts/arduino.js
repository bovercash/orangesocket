;(function(window, $, undefined){
	
	var arduino = (function(){
		var defaults = {};
		//var location = "ws://localhost:8080/arduino";
		var _arduino = function(options){
			if(!(this instanceof _arduino)){
				return new _arduino(options);
			}
			
			var self = this;
			self.config = $.extend({}, defaults, options);
			self._ws=undefined;
			self._status=0;
			return self;
		};
		
		return _arduino;
	})();
	
	arduino.fn = arduino.prototype = {
		_onmessage: function(self,m){
			if (m.data){
				if (self.config.onReceive && $.isFunction(self.config.onReceive)){
					self.config.onReceive(m);
				}
	        }
		},
		_onclose: function(self){
			self._ws=null;
			self._status=0;
			if (self.config.onClose && $.isFunction(self.config.onClose)){
				self.config.onClose();
			}
		},
		connect: function(){
			var self = this;
			try{
				self._ws=new WebSocket(self.config.location);
				self._ws.onmessage=self._onmessage(self);
				self._ws.onclose=self._onclose(self);
				self._status=1;
				if (self.config.onConnect && $.isFunction(self.config.onConnect)){
					self.config.onConnect(self);
				}
			}catch(error){
				if(self.config.onError && $.isFunction(self.config.onError)){
					self.config.onError(self,error);
				}
			}
			
		},
		send: function(m){
			var self = this;
			if(self._ws){
	    		self._ws.send(m);
	    		if (self.config.onSend && $.isFunction(self.config.onSend)){
					self.config.onSend(m);
				}
	    	}
		}
	};
	$.arduino = arduino;	
})(window, jQuery);