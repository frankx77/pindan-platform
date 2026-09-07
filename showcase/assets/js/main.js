/* ============================================================
   Pindan Showcase - 主交互脚本
   ============================================================ */

(function () {
  'use strict';

  // ===================== AOS 初始化 =====================
  if (window.AOS) {
    AOS.init({
      duration: 600,
      easing: 'ease-out-cubic',
      once: true,
      offset: 80,
      disable: false
    });
  }

  // ===================== 导航：滚动时变实色 =====================
  const nav = document.getElementById('nav');
  const NAV_SCROLL_THRESHOLD = 80;

  function handleNavScroll() {
    if (!nav) return;
    if (window.scrollY > NAV_SCROLL_THRESHOLD) {
      nav.classList.add('is-scrolled');
    } else {
      nav.classList.remove('is-scrolled');
    }
  }

  window.addEventListener('scroll', handleNavScroll, { passive: true });
  handleNavScroll();

  // ===================== 导航：滚动高亮当前 section =====================
  const navLinks = document.querySelectorAll('.nav__links a[href^="#"]');
  const sections = Array.from(navLinks)
    .map(link => document.querySelector(link.getAttribute('href')))
    .filter(Boolean);

  function highlightNavLink() {
    const scrollPos = window.scrollY + 120;
    let current = null;
    sections.forEach(sec => {
      if (sec.offsetTop <= scrollPos) {
        current = sec.id;
      }
    });
    navLinks.forEach(link => {
      const target = link.getAttribute('href').slice(1);
      link.classList.toggle('is-active', target === current);
    });
  }

  window.addEventListener('scroll', highlightNavLink, { passive: true });
  highlightNavLink();

  // ===================== 汉堡菜单 =====================
  const burger = document.getElementById('navBurger');
  if (burger && nav) {
    burger.addEventListener('click', () => {
      nav.classList.toggle('is-open');
    });

    // 点击链接后自动收起
    navLinks.forEach(link => {
      link.addEventListener('click', () => {
        nav.classList.remove('is-open');
      });
    });

    // 点击外部关闭
    document.addEventListener('click', e => {
      if (!nav.contains(e.target) && nav.classList.contains('is-open')) {
        nav.classList.remove('is-open');
      }
    });
  }

  // ===================== 功能 Tabs 切换 =====================
  const tabs = document.querySelectorAll('.feature-tab');
  const panels = document.querySelectorAll('.feature-panel');

  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      const target = tab.dataset.tab;

      tabs.forEach(t => t.classList.toggle('is-active', t === tab));
      panels.forEach(p => p.classList.toggle('is-active', p.dataset.panel === target));
    });
  });

  // ===================== Counter 数字滚动动画 =====================
  const counters = document.querySelectorAll('[data-count]');
  const counterObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        animateCounter(entry.target);
        counterObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.3 });

  counters.forEach(counter => counterObserver.observe(counter));

  function animateCounter(el) {
    const target = parseInt(el.dataset.count, 10);
    const duration = 1200;
    const startTime = performance.now();

    function step(now) {
      const elapsed = now - startTime;
      const progress = Math.min(elapsed / duration, 1);
      // ease-out cubic
      const eased = 1 - Math.pow(1 - progress, 3);
      const value = Math.round(target * eased);
      el.textContent = value;
      if (progress < 1) {
        requestAnimationFrame(step);
      } else {
        el.textContent = target;
      }
    }

    requestAnimationFrame(step);
  }

  // ===================== 截图横向拖拽滚动 =====================
  const scrollers = document.querySelectorAll('.screenshots__scroller');
  scrollers.forEach(scroller => {
    let isDown = false;
    let startX = 0;
    let scrollLeft = 0;

    scroller.addEventListener('mousedown', e => {
      isDown = true;
      scroller.style.cursor = 'grabbing';
      startX = e.pageX - scroller.offsetLeft;
      scrollLeft = scroller.scrollLeft;
    });

    scroller.addEventListener('mouseleave', () => {
      isDown = false;
      scroller.style.cursor = 'grab';
    });

    scroller.addEventListener('mouseup', () => {
      isDown = false;
      scroller.style.cursor = 'grab';
    });

    scroller.addEventListener('mousemove', e => {
      if (!isDown) return;
      e.preventDefault();
      const x = e.pageX - scroller.offsetLeft;
      const walk = (x - startX) * 1.5;
      scroller.scrollLeft = scrollLeft - walk;
    });

    // 触屏滑动
    let touchStartX = 0;
    let touchScrollLeft = 0;

    scroller.addEventListener('touchstart', e => {
      touchStartX = e.touches[0].pageX;
      touchScrollLeft = scroller.scrollLeft;
    }, { passive: true });

    scroller.addEventListener('touchmove', e => {
      const x = e.touches[0].pageX;
      const walk = (touchStartX - x) * 1.2;
      scroller.scrollLeft = touchScrollLeft + walk;
    }, { passive: true });

    // 初始 cursor
    scroller.style.cursor = 'grab';
  });

  // ===================== 平滑滚动（兼容老浏览器）====================
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', e => {
      const href = anchor.getAttribute('href');
      if (href === '#' || href === '#top') return;
      const target = document.querySelector(href);
      if (!target) return;

      e.preventDefault();
      const top = target.getBoundingClientRect().top + window.scrollY - 72;
      window.scrollTo({ top, behavior: 'smooth' });
    });
  });

  // ===================== 滚动入场时重新计算 AOS =====================
  // 处理图片加载后位置变化
  window.addEventListener('load', () => {
    if (window.AOS) AOS.refresh();
  });

})();
