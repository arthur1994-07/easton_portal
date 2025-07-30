'use strict'

let production = import.meta.env.VITE_NODE_ENV === 'production';
let prefix_base = import.meta.env.VITE_BASE_URL == null ? null : import.meta.env.VITE_BASE_URL;

const hostname = window.location.hostname
const httpType = window.location.protocol
const port = production ? (httpType == "http:" ? 7777 : 8443) : 7777
const ws = production ? (httpType == "http:" ? 'ws:' : 'wss:') : 'ws:'

let config = {
	$stun_default: 'stun.l.google.com:19302',
	$sock_url: `${ws}//${hostname}:${port}`,
	$api_url: `${httpType}//${hostname}:${port}`,
	$redirect_host: `${httpType}//${window.location.host}`,
	$prefix_base: prefix_base,
}


export { config }