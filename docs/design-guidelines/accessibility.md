
---

## Layout Patterns

### Page Layout

**Standard Page:**
```jsx
<div className="px-4 md:px-6 lg:px-8 py-6 max-w-7xl mx-auto">
  <h1 className="text-2xl font-semibold mb-6">Page Title</h1>
  {/* Content sections */}
</div>
```

### Grid Layouts

**Card Grid:**
```jsx
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
  {items.map(item => (
    <Card key={item.id} {...item} />
  ))}
</div>
```

**Dashboard Grid:**
```jsx
<div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
  <div className="lg:col-span-2">{/* Main content */}</div>
  <div>{/* Sidebar */}</div>
</div>
```

### Flexbox Layouts

**Centered Content:**
```jsx
<div className="flex items-center justify-center h-screen">
  {/* Centered content */}
</div>
```

**Space Between:**
```jsx
<div className="flex items-center justify-between">
  <h2>Title</h2>
  <button>Action</button>
</div>
```

### Empty States

```jsx
<div className="flex flex-col items-center justify-center py-12 text-center">
  <Folder size={48} className="text-gray-400 mb-4" />
  <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
    No projects yet
  </h3>
  <p className="text-gray-600 dark:text-gray-400 mb-4">
    Get started by creating your first project
  </p>
  <button className="px-4 py-2 bg-blue-600 text-white rounded-md">
    Create Project
  </button>
</div>
```

---

## Best Practices

### Consistency

1. **Reuse component patterns:** Don't reinvent common components
2. **Follow established spacing:** Use spacing scale consistently
3. **Maintain color usage:** Use semantic colors for their purpose
4. **Icon sizing:** Use standard sizes (16, 20, 24px)

### Performance

1. **Avoid inline styles:** Use Tailwind classes
2. **Minimize DOM depth:** Keep nesting shallow
3. **Use CSS transitions:** GPU-accelerated
4. **Lazy load images:** Use loading="lazy"

### Maintainability

1. **Extract repeated patterns:** Create reusable components
2. **Comment complex layouts:** Help future developers
3. **Use semantic class names:** When creating custom CSS
4. **Document custom components:** Add prop documentation

### Testing

1. **Test both themes:** Light and dark mode
2. **Test all breakpoints:** Mobile, tablet, desktop
3. **Keyboard navigation:** Tab through forms and modals
4. **Screen reader:** Test with VoiceOver/NVDA

---

## Design Checklist

**Before Implementing a Component:**
- [ ] Color scheme defined (light + dark mode)
- [ ] Typography hierarchy clear
- [ ] Spacing consistent with scale
- [ ] Responsive behavior planned
- [ ] Accessibility considered
- [ ] Interactive states defined (hover, active, focus, disabled)
- [ ] Transitions appropriate

**Before Shipping:**
- [ ] Tested in light and dark mode
- [ ] Tested on mobile, tablet, desktop
- [ ] Keyboard navigation works
- [ ] Focus indicators visible
- [ ] Color contrast meets WCAG AA
- [ ] No console errors/warnings
- [ ] Performance acceptable (no jank)

---

## Resources

### Tools
- [Tailwind CSS Docs](https://tailwindcss.com/docs)
- [Lucide Icons](https://lucide.dev/icons/)
- [Color Contrast Checker](https://webaim.org/resources/contrastchecker/)
- [Responsive Design Checker](https://responsivedesignchecker.com/)
- [WAVE Accessibility Tool](https://wave.webaim.org/)

### References
- [Material Design](https://m3.material.io/) (inspiration)
- [Apple Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)

### Community
- [Tailwind UI](https://tailwindui.com/) (component examples)
- [Headless UI](https://headlessui.com/) (accessible components)
- [Radix UI](https://www.radix-ui.com/) (primitive components)
