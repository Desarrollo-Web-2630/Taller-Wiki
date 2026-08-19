function toggleSection(btn) {
    const li = btn.closest('li');
    const submenu = li ? li.querySelector('.submenu') : null;

    if (!submenu) return;
    const expanded = submenu.style.display !== 'none';
    submenu.style.display = expanded ? 'none' : 'block';
    btn.innerHTML = expanded ? '&#9656;' : '&#9662;';
    btn.setAttribute('aria-expanded', !expanded);
}

document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.submenu').forEach(function(ul) {
    ul.style.display = 'none';
    });

    document.querySelectorAll('.toggle-btn').forEach(function(btn) {
    btn.innerHTML = '&#9656;';
    btn.setAttribute('aria-expanded', 'false');
  });

  document.querySelectorAll('.active').forEach(function(el) {
    let parent = el.closest('.submenu');
    while (parent) {
      parent.style.display = 'block';
      
      const toggleBtn = parent.previousElementSibling?.querySelector('.toggle-btn');
      if (toggleBtn) {
        toggleBtn.innerHTML = '&#9662;';
        toggleBtn.setAttribute('aria-expanded', 'true');
      }
      
      parent = parent.parentElement ? parent.parentElement.closest('.submenu') : null;
    }
  });
});