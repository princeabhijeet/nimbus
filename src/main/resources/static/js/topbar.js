// Topbar dropdown behavior shared across pages
(function(){
  function initTopbar(){
    console.debug && console.debug('[topbar] initTopbar');
    // helper to find closest ancestor matching selector
    function closest(el, selector){ while(el && el !== document){ if(el.matches && el.matches(selector)) return el; el = el.parentNode; } return null; }

    function openMenu(menu){ if(!menu) return; const dropdown = menu.querySelector('.user-dropdown'); const btn = menu.querySelector('.user-btn'); if(dropdown){ dropdown.setAttribute('aria-hidden','false'); btn?.setAttribute('aria-expanded','true'); dropdown.setAttribute('tabindex','-1'); try{ dropdown.focus(); }catch(e){} }}
    function closeMenu(menu){ if(!menu) return; const dropdown = menu.querySelector('.user-dropdown'); const btn = menu.querySelector('.user-btn'); if(dropdown){ dropdown.setAttribute('aria-hidden','true'); btn?.setAttribute('aria-expanded','false'); }}
    function closeAllMenus(){ document.querySelectorAll('.user-menu').forEach(closeMenu); }

    // Click handler via delegation
    document.addEventListener('click', function(e){
      const clickedBtn = closest(e.target, '.user-btn');
      if(clickedBtn){
        console.debug && console.debug('[topbar] clicked user-btn', clickedBtn);
        // toggle the menu for this button
        const menu = closest(clickedBtn, '.user-menu');
        if(!menu) return;
        const dropdown = menu.querySelector('.user-dropdown');
        const isOpen = dropdown && dropdown.getAttribute('aria-hidden') === 'false';
        if(isOpen){ closeMenu(menu); }
        else { closeAllMenus(); openMenu(menu); }
        e.preventDefault();
        return;
      }

      // handle logout click via id or data-action
      const logoutEl = closest(e.target, '#dropdownLogout') || closest(e.target, '[data-action="logout"]');
      if(logoutEl){
        console.debug && console.debug('[topbar] logout clicked');
        // perform logout
        e.preventDefault(); closeAllMenus(); (async ()=>{ try{ await fetch('/nimbus/api/v1/auth/logout',{ method:'POST', credentials:'same-origin' }); }catch(err){ console.warn('Logout request failed', err); } finally { window.location = '/nimbus/login'; } })();
        return;
      }

      // click outside any .user-menu -> close all
      if(!closest(e.target, '.user-menu')){ closeAllMenus(); }
    });

    // Escape key closes any open menu
    document.addEventListener('keydown', function(e){ if(e.key === 'Escape'){ closeAllMenus(); } });
  }

  if (document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', initTopbar);
  } else {
    initTopbar();
  }
})();
