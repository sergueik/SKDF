// https://stackoverflow.com/questions/14137129/find-closest-element-with-display-block
// https://gomakethings.com/how-to-get-the-closest-parent-element-with-a-matching-selector-using-vanilla-javascript/ 
closesrParentBlockNode = function(element) {
  for (; element && element !== document; element = element.parentNode) {
    var elementStyle = element.currentStyle || window.getComputedStyle(element, "");
    var displayType = elementStyle.display;
    // https://www.w3schools.com/jsref/prop_style_display.asp
    if (displayType =~/block/) {
      return element;
    }
  }
  return null;
};

var element = arguments[0];

return closesrParentBlockNode(element);