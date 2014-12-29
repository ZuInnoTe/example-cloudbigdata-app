/**
* The MIT License (MIT)
*
* Copyright (c) 2014 ZuInnoTe (zuinnote@gmail.com) / Jörn Franke

* Permission is hereby granted, free of charge, to any person obtaining a copy of
* this software and associated documentation files (the "Software"), to deal in
* the Software without restriction, including without limitation the rights to
* use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
* the Software, and to permit persons to whom the Software is furnished to do so,
* subject to the following conditions:

* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.

* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
* FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
* COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
* IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
* CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

/**
* This is a basic WebRTC library to communicate via Voice/Video-Chat or Peer-to-Peer Exchange with other users or machines (e.g. peer to peer sensor data).
* Requires sock.js to communicate and jQuery to place incoming/outgoing videos in HTML page
*
*/

var webrtc_error_codes = {
	USERMEDIA_NOTSUPPORTED: 0,
	DATACHANNEL_NOTSUPPORTED: 1,
	ENABLE_USERMEDIA_FAILED:2,
	CREATEPEERCONNECTION_FAILED:3,
	CREATESESSIONDESCRIPTOR_FAILED:4,
	NODATACHANNEL:5,
	DATACHANNEL_ERROR:6,
	CREATEDATACHANNEL_FAILED: 7
}

var webrtc_error_messages = {
	USERMEDIA_NOTSUPPORTED: "Error: getUserMedia not supported by browser",
	DATACHANNEL_NOTSUPPORTED: "Error: data channel not supported by browser",
	ENABLE_USERMEDIA_FAILED: "Error: Cannot enable user media",
	CREATEPEERCONNECTION_FAILED: "Error: Cannot create RTCPeerConnection",
	CREATESESSIONDESCRIPTOR_FAILED: "Error: Cannot create Session Descriptor",
	NODATACHANNEL: "Error: Cannot send message. No data channel exist to peer",
	DATACHANNEL_ERROR: "Error: Cannot establish data channel",
	CREATEDATACHANNEL_FAILED: "Error Cannot create RTCDataChannel",
}


var webrtc_status_codes = {
	INITIALIZE: 0,
	INITIALIZE_CHAT: 1,
	INITIALIZE_DATA: 2,
	INITIALIZE_USERMEDIA: 3,
	STARTING_CALL: 4,
	ACCEPTED_CALL: 5,
	RECEIVED_CALL: 6,
	HANGUP_CALL: 7,
	CALL_OPEN: 8,
	DATACHANNEL_OPEN: 9,
	DATACHANNEL_CLOSE: 10,
}

var webrtc_status_messages = {
	INITIALIZE: "Initializing WebRTC",
	INITIALIZE_CHAT: "Initializing WebRTC Video/Voice Chat",
	INITIALIZE_DATA: "Initializing WebRTC Data Channel",
	INITIALIZE_USERMEDIA: "Initializing User Media",
	STARTING_CALL: "Starting Call",
	ACCEPTED_CALL: "The following user accepted your call: ",
	RECEIVED_CALL: "Received call from the following user: ",
	HANGUP_CALL: "The following user hanged up your call: ",
	CALL_OPEN: "Call is opened with the following user: ",
	DATACHANNEL_OPEN: "Data channel openend to the following user: ",
	DATACHANNEL_CLOSE: "Data channel opened to the following user: "
}

var webrtclocalstream=null;

/** Peer object storing information about other users **/
function Peer(userid,webrtcconfig) {
	this.userid=userid;
	this.config=webrtcconfig;
	this.dataChannel=null;
	this.pc=null;
	this.connected=false;
	var thisPeer=this;
	console.log('WebRTC: Creating RTCPeerConnection with \n config='+webrtcconfig.pc_config+'\n constraints='+webrtcconfig.pc_constraints);
	try {
		this.pc=new RTCPeerConnection(webrtcconfig.pc_config, webrtcconfig.pc_constraints);
		if (webrtclocalstream!=null) {
			this.pc.addStream(webrtclocalstream);
		}
	} catch (e) {
		webrtcconfig.error_handler(webrtc_error_codes.CREATEPEERCONNECTION_FAILED, webrtc_error_messages.CREATEPEERCONNECTION_FAILED,this.userid);
		console.log('WebRTC: Error creating RTCPeerConnection: '+e);
		return;
	}
	/** call this peer **/
	this.callPeer=function() {
		if (thisPeer.config.enable_datachannel==true) {
			thisPeer.enableDataChannel(thisPeer.config.datachannel_name);
		}
		thisPeer.pc.createOffer(function(sessionDescriptor) {		
					thisPeer.pc.setLocalDescription(sessionDescriptor,
						function() {
							console.log('WebRTC: Set Session Descriptor succesful for user "'+thisPeer.userid+'"');
						}, 
						function(error) {
							console.log('WebRTC: Set Session Descriptor error for user "'+thisPeer.userid+'": '+error);
						});
						thisPeer.sendSignalingMessage('CALL',sessionDescriptor);
					},
					function(error) {
						webrtcconfig.error_handler(webrtc_error_codes.CREATESESSIONDESCRIPTOR_FAILED, webrtc_error_messages.CREATESESSIONDESCRIPTOR_FAILED,this.userid);
						console.log("WebRTC: Cannot create session descriptor: "+error);
					}, webrtcconfig.receiveConstraints);
	}

	/** answer to a call of this peer **/
	this.answerPeer=function() {
	
		thisPeer.pc.createAnswer(function(sessionDescriptor) {		
					thisPeer.pc.setLocalDescription(sessionDescriptor,
						function() {
							console.log('WebRTC: Set Session Descriptor succesful for user "'+thisPeer.userid+'"');
						}, 
						function(error) {
							console.log('WebRTC: Set Session Descriptor error for user "'+thisPeer.userid+'": '+error);
						}
					);
					thisPeer.sendSignalingMessage('ANSWER',sessionDescriptor);
				},
				function(error) {
					webrtcconfig.error_handler(webrtc_error_codes.CREATESESSIONDESCRIPTOR_FAILED, webrtc_error_messages.CREATESESSIONDESCRIPTOR_FAILED,this.userid);
					console.log("WebRTC: Cannot create session descriptor: "+error);
				},webrtcconfig.receiveConstraints);
	}

	/** Hangup peer **/
	this.hangupPeer=function() {		
		thisPeer.pc.close();
		thisPeer.pc=null;
		thisPeer.onRemoteStreamRemoved();
	}
	/** Enable Data Channel **/
	this.enableDataChannel=function(name) {
		console.log("WebRTC: Enabling Data Channel with user "+thisPeer.userid);
		try {
			thisPeer.dataChannel = thisPeer.pc.createDataChannel(webrtcconfig.datachannel_name, webrtcconfig.datachannel_options);
			console.log("Created Data Channel: "+webrtcconfig.datachannel_name);
		}
		 catch (e) {
			webrtcconfig.error_handler(webrtc_error_codes.CREATEDATACHANNEL_FAILED, webrtc_error_messages.CREATEDATACHANNEL_FAILED,this.userid);
			console.log('WebRTC: Error creating RTCDataChannel: '+e);
			return;
		}
		thisPeer.dataChannel.onerror = function (error) {
			webrtcconfig.error_handler(webrtc_error_codes.DATACHANNEL_ERROR, webrtc_error_messages.DATACHANNEL_ERROR,thisPeer.userid);
 			 console.log("Data Channel Error:"+ error);
		};

		thisPeer.dataChannel.onmessage = function (event) {
  			console.log("Got Data Channel Message:", event.data);
			var theMessage= {};
			theMessage['fromUserID']=thisPeer.userid;
			theMessage['body']=event.data;
			webrtcconfig.receivedata_handler(theMessage);
		};
		thisPeer.dataChannel.onopen = function () {
 			webrtcconfig.status_handler(webrtc_status_codes.DATACHANNEL_OPEN, webrtc_status_messages.DATACHANNEL_OPEN,thisPeer.userid);
		 
		};

		thisPeer.dataChannel.onclose = function () {
 		 	console.log("The Data Channel is Closed");
			thisPeer.dataChannel=null;
			webrtcconfig.status_handler(webrtc_status_codes.DATACHANNEL_CLOSE, webrtc_status_messages.DATACHANNEL_CLOSE,thisPeer.userid);
		};
	}
	this.disableDataChannel=function() {
		if (thisPeer.dataChannel!=null) {
			thisPeer.dataChannel.close();
			webrtcconfig.status_handler(webrtc_status_codes.DATACHANNEL_CLOSE, webrtc_status_messages.DATACHANNEL_CLOSE,thisPeer.userid);
		}
	}
	/** Send Message through data channel **/
	this.sendMessageDataChannel=function(message) {
		if (thisPeer.dataChannel==null) {
			webrtcconfig.error_handler(webrtc_error_codes.NODATACHANNEL, webrtc_error_messages.NODATACHANNEL,thisPeer.userid);
			return;
		}
		console.log("Sending data channel message to peer "+thisPeer.userid); 
		console.log(message);
		thisPeer.dataChannel.send(message);
	}

	/** WebRTC functions **/
	this.sendSignalingMessage=function(type,message) {
		var signalingmessage={};
		signalingmessage['type']=type;
		signalingmessage['message']=message;
		signalingmessage['toUserID']=thisPeer.userid;
		signalingmessage['fromUserID']=webrtcconfig.ownUserId;
		// up to the web site to select the correct channel
		webrtcconfig.sendsignalingmessage_handler(signalingmessage);
	}
	
	this.onIceCandidate=function (event) {
		if (event.candidate) {
			event.candidate.type='candidate';
			thisPeer.sendSignalingMessage('SIGNALING',event.candidate);
		
		} else {
			console.log('WebRTC: no further ice candidates');
		}
	};
	this.onRemoteStreamAdded=function (event) {
			webrtcconfig.status_handler(webrtc_status_codes.ACCEPTED_CALL, webrtc_status_messages.ACCEPTED_CALL+thisPeer.userid,thisPeer.userid);
			// remove existing local video
			$('#video_'+this.userid).remove();
			// create the video tag for local video
			var remotevideo = document.createElement('video');
			remotevideo.setAttribute('id', 'video_'+thisPeer.userid);
			// attach stream to it
			remotevideo.autoplay='autoplay';
			remotevideo.className=webrtcconfig.class_outgoing;
			// insert it according to selector
			$(webrtcconfig.video_incoming).append(remotevideo);
			attachMediaStream(remotevideo,event.stream);
	};
	this.onRemoteStreamRemoved=function() {
		// remove the video of the user
		$('#video_'+thisPeer.userid).remove();
	}
	this.onDataChannel=function (event) {
		console.log("WebRTC: Data Channel added by user "+thisPeer.userid);
		console.log(event);
		thisPeer.dataChannel = event.channel;
		thisPeer.dataChannel.onmessage = function (event) {
      			console.log("Got Data Channel Message:", event.data);
			var theMessage= {};
			theMessage['fromUserID']=thisPeer.userid;
			theMessage['body']=event.data;
			webrtcconfig.receivedata_handler(theMessage);
    		};

    		thisPeer.dataChannel.onopen = function () {
        		webrtcconfig.status_handler(webrtc_status_codes.DATACHANNEL_OPEN, webrtc_status_messages.DATACHANNEL_OPEN,thisPeer.userid);
    		};
    		thisPeer.dataChannel.onclose = function (e) {
        		 console.log("The Data Channel is Closed");
			thisPeer.dataChannel=null;
			webrtcconfig.status_handler(webrtc_status_codes.DATACHANNEL_CLOSE, webrtc_status_messages.DATACHANNEL_CLOSE,thisPeer.userid);
    		};
    		thisPeer.dataChannel.onerror = function (e) {
        		 webrtcconfig.error_handler(webrtc_error_codes.DATACHANNEL_ERROR, webrtc_error_messages.DATACHANNEL_ERROR,thisPeer.userid);
 			 console.log("Data Channel Error:"+ error);
    		};
	}
	this.onSignalingStateChanged=function () {
		if (thisPeer.pc) {
			console.log('WebRTC: Signaling state changed to: ' + thisPeer.pc.signalingState);
		}
	}
	this.onIceConnectionStateChanged= function() {
		if (thisPeer.pc) {
			console.log('WebRTC: ICE connection state changed to: ' + thisPeer.pc.iceConnectionState);
			if (thisPeer.pc.iceConnectionState == 'connected') {
				thisPeer.connected=true;
				webrtcconfig.status_handler(webrtc_status_codes.CALL_OPEN, webrtc_status_messages.CALL_OPEN+thisPeer.userid,thisPeer.userid);
			}
 			if (thisPeer.pc.iceConnectionState == 'disconnected') {
				thisPeer.connected=false;
        			$('#video_'+thisPeer.userid).remove();
				webrtcconfig.status_handler(webrtc_status_codes.HANGUP_CALL, webrtc_status_messages.HANGUP_CALL+thisPeer.userid,thisPeer.userid);
			}
		
    		}
	}

	this.processSignalingMessage=function(message) {
		if (!thisPeer.pc) {
			console.log("WebRTC: Received signaling message for a non-existing PeerConnection");		
			return;
		}
		if (message.type === 'offer') {
			 thisPeer.pc.setRemoteDescription(new RTCSessionDescription(message),
						function() {
							console.log('WebRTC: Set Session Descriptor succesful (offer) for user "'+thisPeer.userid+'"');
						}, 
						function(error) {
							console.log('WebRTC: Set Session Descriptor error (offer) for user "'+thisPeer.userid+'": '+error);
						}
			);
		} else if (message.type === 'answer') {
				thisPeer.pc.setRemoteDescription(new RTCSessionDescription(message),
						function() {
							console.log('WebRTC: Set Session Descriptor succesful (answer) for user "'+thisPeer.userid+'"');
						}, 
						function(error) {
							console.log('WebRTC: Set Session Descriptor error (answer) for user "'+thisPeer.userid+'": '+error);
						}
				
			);
		} else if (message.type === 'candidate') {
			thisPeer.pc.addIceCandidate(new RTCIceCandidate(message),function() {
							console.log('WebRTC: OnAddIceCandidate succesful for user "'+thisPeer.userid+'"');
						}, 
						function(error) {
							console.log('WebRTC: OnAddIceCandidate error for user "'+thisPeer.userid+'": '+error);
						});
		} else {
			console.log('WebRTC: Received unknown signaling message type: ' + message);
		}
	}
	this.pc.onicecandidate = this.onIceCandidate;
	this.pc.ondatachannel=this.onDataChannel;
	this.pc.onaddstream = this.onRemoteStreamAdded;
	this.pc.onremovestream = this.onRemoteStreamRemoved;
	this.pc.onsignalingstatechange = this.onSignalingStateChanged;
	this.pc.oniceconnectionstatechange = this.onIceConnectionStateChanged;


	
}

/** WebRTC object **/

function WebRTC(webrtcconfig) {
	/** define object data **/
	this.config = webrtcconfig; /* configuration */
	this.peers = []; 	/* connected peers */
	var thisWebRTC=this;
	/** define object functions **/
	/*** Video/Voice functions **/
	this.enableUserMedia=function() {
		webrtcconfig.status_handler(webrtc_status_codes.INITIALIZE_USERMEDIA, webrtc_status_messages.INITIALIZE_USERMEDIA);
		var getUserMedia = navigator.getUserMedia || navigator.mozGetUserMedia || navigator.webkitGetUserMedia;
		if (getUserMedia) {
    			getUserMedia = getUserMedia.bind(navigator);
		} else {
			webrtcconfig.error_handler(webrtc_error_codes.USERMEDIA_NOTSUPPORTED, webrtc_error_messages.USERMEDIA_NOTSUPPORTED);
	    	}

		// then deal with a weird, positional error handling API
		getUserMedia(this.config.user_media, 
    		// success callback
    		function (stream) {
        		// remove existing local video
			$('#video_'+webrtcconfig.ownUserId).remove();
			webrtclocalstream=stream;
			// create the video tag for local video
			var localvideo = document.createElement('video');
			localvideo.setAttribute('id', 'video_'+webrtcconfig.ownUserId);
			// attach stream to it
			attachMediaStream(localvideo,stream);
			localvideo.autoplay='autoplay';
			localvideo.className=webrtcconfig.class_outgoing;
			localvideo.muted=true; // no need to hear own voice
			// insert it according to selector
			$(webrtcconfig.video_outgoing).append(localvideo);
   		 }, 
   		 // error callback
    		function (error) {
       			 // called if failed to get media
			webrtcconfig.error_handler(webrtc_error_codes.ENABLE_USERMEDIA_FAILED, webrtc_error_messages.ENABLE_USERMEDIA_FAILED);
   		 }
		);
	}
	/** disable own video / voice recording/transmission **/
	this.disableUserMedia=function() {
		webrtclocalstream.stop();
		var localvideo=	document.getElementById('video_'+webrtcconfig.ownUserId);
		if (localvideo!=null) {
			dettachMediaStream(localvideo);
		}
	}
	/*** Video/Voice Chat functions ***/
        
	/** Call a user via signaling channel **/
	/**
	    destinationid: identifier (principal name) of remote user **/
	this.callChat=function(destinationid) {
		webrtcconfig.status_handler(webrtc_status_codes.STARTING_CALL, webrtc_status_messages.STARTING_CALL);
		// Create peer partner
		console.log('WebRTC: Creating RTCPeerConnection for starting call between "'+webrtcconfig.ownUserId+'" and "'+destinationid+'"...');
		if (thisWebRTC.peers[destinationid]==null) {
			thisWebRTC.peers[destinationid]=new Peer(destinationid,webrtcconfig); // add to connected peers and create rtc peer connection
		}		
		thisWebRTC.peers[destinationid].callPeer();

	}

	/** 
	    signalingmessage: offer by remote user **/
	this.answerchat=function(signalingmessage) {
		
		
		console.log(signalingmessage);
		if (thisWebRTC.peers[signalingmessage['fromUserID']]==null) {
			thisWebRTC.peers[signalingmessage['fromUserID']]=new Peer(signalingmessage['fromUserID'],webrtcconfig); // add to connected peers and create rtc peer connection
		}
		console.log(thisWebRTC.peers[signalingmessage['fromUserID']]);
		thisWebRTC.peers[signalingmessage['fromUserID']].processSignalingMessage(signalingmessage['message']);
		thisWebRTC.peers[signalingmessage['fromUserID']].answerPeer();
		
		
	}
	this.closeChat=function(destinationid) {
		if (thisWebRTC.peers[destinationid]==null) return;
		thisWebRTC.peers[destinationid].hangupPeer();
		thisWebRTC.peers[destinationid]=null;
	}
	/** processes a signaling message from another user **/
	this.processSignalingMessage=function(signalingmessage) {
		if (thisWebRTC.peers[signalingmessage['fromUserID']]==null) {
			thisWebRTC.peers[signalingmessage['fromUserID']]=new Peer(signalingmessage['fromUserID'],webrtcconfig); // add to connected peers and create rtc peer connection
		}
		thisWebRTC.peers[signalingmessage['fromUserID']].processSignalingMessage(signalingmessage['message']);
	}
	/*** Data Channnel functions ***/  
	
	this.sendDataChannel=function(destinationid,message) {
		thisWebRTC.peers[destinationid].sendMessageDataChannel(message);
	}

	this.disableDataChannel=function(destinationid) {
		thisWebRTC.peers[destinationid].disableDataChannel();
	}

	/** Constructor init **/
	webrtcconfig.status_handler(webrtc_status_codes.INITIALIZE, webrtc_status_messages.INITIALIZE);

	
	

}


