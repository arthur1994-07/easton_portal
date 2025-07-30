'use strict'


const concatingPath =  (path, child) => {
	if (path == null) return child;
	if (path.length > 0 && path.slice(-1) == "/") path = path.slice(0, -1);
	if (child === "/") child ='';
	return `${path}/${child}`;
}

const addFullPathRecursively = (items, path) => {
	items.forEach(item => {
		if (item.meta == null) item.meta = {}

		item.meta.fullPath = concatingPath(path, item.path);
		if (item.children != null) {
			addFullPathRecursively(item.children, item.meta.fullPath);
		}
	})
}

const getAllItemsRecursively = (items) => {
	var result = []
	for(let i=0; i<items.length; i++) {
		result.push(items[i]);
		if (items[i].children != null) {
			result = [...result, ...getAllItemsRecursively(items[i].children)];
		}
	}
	return result;
} 

const getTreeRecursively = (route) => {
	var name = (route.children != null && route.children.length != 0) ? null : route.name
	return {
		...route,
		name: name,
		component: route.component,
		children : route.children?.map(child => getTreeRecursively(child)),
	};
}


export const resolveFullPath = (treeItems) => {
	addFullPathRecursively(treeItems, null);
	return treeItems;
}

export const getPathParent = (path, level) => {
	let array = path.split('/')
	return ('/'.concat(array.slice(1, level + 1 ).join('/')))
}

export const getRouteList = (treeItems) => {
	return getAllItemsRecursively(treeItems)
}

export const getRouteTree = (treeItem) => {
	return treeItem.map(route => getTreeRecursively(route));
}

