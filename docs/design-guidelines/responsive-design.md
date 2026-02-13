```jsx
<button className="py-3 px-4 md:py-2 md:px-4">
  {/* Larger padding on mobile */}
</button>
```

---

## Accessibility

### WCAG 2.1 AA Compliance

**Color Contrast:**
- Normal text (< 18pt): 4.5:1 minimum
- Large text (≥ 18pt or bold ≥ 14pt): 3:1 minimum
- UI components: 3:1 minimum

**Verify Contrast:**
- Use browser DevTools or online tools
- Test in both light and dark modes

### Keyboard Navigation

**Focus Indicators:**
```jsx
<button className="focus:outline-none focus:ring-2 focus:ring-blue-500
                   focus:ring-offset-2">
  Button
</button>
```

**Tab Order:**
- Use semantic HTML for natural tab order
- Avoid `tabindex` > 0
- Ensure modals trap focus

### Semantic HTML

**Use appropriate elements:**
```jsx
<nav>...</nav>          {/* Navigation */}
<main>...</main>        {/* Main content */}
<article>...</article>  {/* Independent content */}
<section>...</section>  {/* Thematic grouping */}
<button>...</button>    {/* Interactive action */}
<a href="...">...</a>   {/* Navigation link */}
```

### ARIA Labels

**Icon Buttons:**
```jsx
<button aria-label="Close dialog" onClick={onClose}>
  <X size={20} />
</button>
```

**Search Input:**
```jsx
<input
  type="search"
  aria-label="Search tasks"
  placeholder="Search..."
/>
```

**Loading States:**
```jsx
<div role="status" aria-live="polite">
  Loading...
</div>
```

### Screen Reader Support

**Hide Decorative Elements:**
```jsx
<div aria-hidden="true">
  {/* Decorative icon or image */}
</div>
```

**Announce Dynamic Content:**
```jsx
<div role="alert" aria-live="assertive">
  {/* Error message */}
</div>
```

---

## Animation & Transitions

### Transition Guidelines

**Duration:**
- Fast transitions: 150ms (hover states)
- Standard transitions: 200-300ms (most UI changes)
- Slow transitions: 500ms+ (modals, page transitions)

**Easing:**
- Default: ease-in-out
- Enter: ease-out
- Exit: ease-in

### Tailwind Transitions

**Color Transitions:**
```jsx
<button className="bg-blue-600 hover:bg-blue-700 transition-colors duration-200">
  Button
</button>
```

**Shadow Transitions:**
```jsx
<div className="shadow-md hover:shadow-lg transition-shadow duration-300">
  Card
</div>
```

**Transform Transitions:**
```jsx
<div className="transform hover:scale-105 transition-transform duration-200">
  Scalable element
</div>
```

**Opacity Transitions:**
```jsx
<div className="opacity-0 hover:opacity-100 transition-opacity duration-300">
  Fade in
</div>
```

### Animation Best Practices

1. **Respect user preferences:** Use `prefers-reduced-motion`
2. **Keep it subtle:** Avoid distracting animations
3. **Performance:** Use transform/opacity (GPU-accelerated)
4. **Purpose:** Animations should guide attention or indicate state

```jsx
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```
