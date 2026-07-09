## Reflexion


- ¿Qué partes generó bien la IA sin necesidad de corrección?
El crud inicial, el dockerfile , el docker compose , necesito correciones para la parte de seguridad.
- ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en temas de seguridad?
    Tuve que agregar un rol manualmente al ENUM
- ¿Cómo detectaron esos errores y cómo los corrigieron?
    Se tuvo que agregar manualmente el campo de hotelId para Recepcionista y su relacion one-to-one.
- Si tuvieran que explicarle a un compañero cómo funciona el mecanismo de autorización por sucursal (o la regla de negocio que eligieron), ¿qué le dirían en 3-4 líneas?
 Basicamente significa que para el rol de RECEPCIONIST , tiene habilitado solo ver las reservaciones y cuartos al hotel que pertenece no esta autorizado para alterar reservaciones de otros hoteles.