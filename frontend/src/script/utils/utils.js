'use strict'

import yaml from 'js-yaml'

export const showFileDialog = async (accept) => {
	return new Promise((resolve) => {
		const input = document.createElement('input')
		input.setAttribute("type", "file")
		input.setAttribute("style", "display: none")
		if (accept != null) input.setAttribute("accept", accept)

		input.onchange = () => {
			const file = input.files[0]
			document.body.removeChild(input)
			resolve(file);
		};

		document.body.appendChild(input)
		setTimeout(() => {
			input.click();
			const onFocus = () => {
				window.removeEventListener('focus', onFocus);
				document.body.addEventListener('mousemove', onMouseMove);
			};
			const onMouseMove = () => {
				document.body.removeEventListener('mousemove', onMouseMove);
				if (!input.files.length) {
					document.body.removeChild(input);
					resolve(null);
				}
			}
			window.addEventListener('focus', onFocus);
		}, 0);
	});
}

export const fileToDataUrl = async (file) => {
	return new Promise((resolve, reject) => {
		if (file === null || !(file instanceof Blob)) {
			reject(new Error('Blob is null'))
		}
		let reader = new FileReader()
		reader.onload = (e) => {
			resolve(e.target.result)
		}
		reader.readAsDataURL(file)
	})
}


export const dataUrlToBase64String = (dataURL) => {
	if (!dataURL) return ''

	const type = dataURL.split(';')[1].split(',')[0]
	if (type != 'base64') return null

	return dataURL.split(',')[1]
}

export const base64ToUint8Array = (base64) => {
	const binaryString = window.atob(base64)
	const bytes = new Uint8Array(binaryString.length)
	for (var i = 0; i < binaryString.length; i++) {
		bytes[i] = binaryString.charCodeAt(i)
	}
	return bytes
}

export const base64toBlob = (b64Data, contentType='', sliceSize=512) => {
	const byteCharacters = window.atob(b64Data);
	const byteArrays = []
	for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
		const slice = byteCharacters.slice(offset, offset + sliceSize);
		const byteNumbers = new Array(slice.length);
		for (let i = 0; i < slice.length; i++) {
			byteNumbers[i] = slice.charCodeAt(i);
		}
		byteArrays.push(new Uint8Array(byteNumbers))
	}
	return new Blob(byteArrays, {type: contentType})
}

export const fileToText = async (file) => {
	return new Promise((resolve, reject) => {
		if (file === null || !(file instanceof File)) {
			reject(new Error('File is null'))
		}
		let reader = new FileReader()
		reader.onload = (e) => {
			resolve(e.target.result)
		}
		reader.readAsText(file)
	})
}


export const parseYamlFile = async (file) => {
	var content = await fileToText(file)
	return yaml.load(content)
}
