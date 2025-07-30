'use strict'

export const targetPath = 'target-path'

export const retrieveValue = (key) => {
	const item = window.sessionStorage.getItem(key)
	window.sessionStorage.removeItem(key)
	return item 
}

export const setValue = (key, value) => {
	window.sessionStorage.setItem(key, value)
}

 
