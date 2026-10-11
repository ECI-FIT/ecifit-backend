## REQ+
| ID | Área funcional | Requerimiento funcional | Especificación y variaciones | Contemplado en S1 | ¿En Figma S1? | Verificado por |
| --- | --- | --- | --- | --- | --- | --- |
| RF-01 | Identidad y personaje | Autenticar estudiante mediante correo institucional ECI | Permitir el acceso únicamente con una cuenta institucional válida. | SI | SI | NaN |
| RF-02 | Identidad y personaje | Crear personaje del estudiante | Solicitar la creación del personaje en el primer ingreso y asociarlo al perfil del estudiante. | SI | Si pero no al iniciar | NaN |
| RF-03 | Identidad y personaje | Seleccionar rol de temporada | Permitir elegir Tanque, Corredor o Estratega para la temporada activa. | SI | Si | NaN |
| RF-04 | Identidad y personaje | Aplicar bonificación según el rol seleccionado | Aplicar automáticamente el multiplicador cuando la actividad coincida con la especialidad del rol. | SI | Si | NaN |
| RF-05 | Identidad y personaje | Actualizar estadísticas del personaje | Modificar Fuerza, Cardio, Flexibilidad y Nutrición según las actividades registradas. | NO | El personaje solo es visible, no tiene estadisticas. | NaN |
| RF-06 | Actividad física | Registrar sesión de entrenamiento de fuerza | Registrar fecha, duración e intensidad de la sesión. | SI pero no tan especifico | Si (seccion Registrar) | NaN |
| RF-07 | Actividad física | Registrar actividad cardiovascular | Permitir registrar trote, bicicleta, natación u otra actividad aeróbica. | SI pero no tan especifico | Si (seccion Registrar) | NaN |
| RF-08 | Actividad física | Registrar sesión de flexibilidad o movilidad | Guardar fecha, duración y tipo de ejercicio realizado. | SI pero no tan especifico | Si (seccion Registrar) | NaN |
| RF-09 | Actividad física | Validar duración mínima de una actividad puntuable | Impedir la asignación de puntos cuando la actividad no cumpla el tiempo mínimo definido. | Si pero con tiempo min de 10 mins | SI (barra ajustable) | NaN |
| RF-10 | Actividad física | Otorgar puntos por actividad física válida | Calcular y acreditar puntos según tipo, duración, intensidad y rol del estudiante. | Si | Si | NaN |
| RF-11 | Actividad física | Otorgar puntos adicionales por asistencia al gimnasio ECI | Aplicar la bonificación cuando la asistencia haya sido confirmada. | No | No | NaN |
| RF-12 | Nutrición | Registrar comida consumida por el estudiante | Guardar el tipo de comida, fecha y componentes principales. | No (solo comida del comedor) | No | NaN |
| RF-13 | Nutrición | Clasificar un registro como comida balanceada | Evaluar el registro según los criterios nutricionales establecidos. | No | No | NaN |
| RF-14 | Nutrición | Otorgar puntos por registro nutricional válido | Acreditar puntos solo cuando el registro cumpla los criterios definidos. | Si pero funciona con cualquier comida | A medias | NaN |
| RF-15 | Nutrición | Consultar menú disponible del comedor ECI | Mostrar las opciones vigentes y su información nutricional disponible. | Si | Si | NaN |
| RF-16 | Misiones y rachas | Generar misiones diarias para los estudiantes | Publicar objetivos diarios según la temporada y el perfil del participante. | Si | SI (Pestaña misiones) | NaN |
| RF-17 | Misiones y rachas | Generar misiones semanales | Publicar objetivos de mayor alcance con fecha límite semanal. | Si | SI (Pestaña misiones) | NaN |
| RF-18 | Misiones y rachas | Registrar cumplimiento de una misión | Validar la evidencia o actividad que satisface el objetivo. | A medias | No hay validacion como tal (Fotografias) | NaN |
| RF-19 | Misiones y rachas | Otorgar puntos al completar una misión | Acreditar la recompensa definida una sola vez por misión. | Si | No se ve la actualizacion de puntos | NaN |
| RF-20 | Misiones y rachas | Calcular racha de actividad consecutiva | Contabilizar días consecutivos con actividad válida. | Si | Si | NaN |
| RF-21 | Misiones y rachas | Otorgar bonificación por alcanzar una racha de siete días | Acreditar la bonificación al completar siete días consecutivos. | Si | No visible | NaN |
| RF-22 | Ligas y temporadas | Crear temporada asociada al semestre académico | Definir fechas de inicio y cierre y asociarlas al semestre correspondiente. | Si | Solamente estetico en el modo admin | NaN |
| RF-23 | Ligas y temporadas | Acumular puntos del estudiante durante la temporada | Consolidar únicamente los puntos obtenidos dentro del periodo activo. | Si | SI | NaN |
| RF-24 | Ligas y temporadas | Calcular liga individual del estudiante | Asignar Bronce, Plata, Oro, Platino o Diamante según los umbrales vigentes. | Si | Si se ve el progreso para ir a la siguiente liga | NaN |
| RF-25 | Ligas y temporadas | Recalcular semanalmente la liga | Actualizar la clasificación individual en el corte semanal. | A medias | No | NaN |
| RF-26 | Ligas y temporadas | Ascender estudiante de liga al cumplir el umbral | Mover al estudiante a la liga superior cuando alcance las condiciones. | Si | No se ve explicitamente | NaN |
| RF-27 | Ligas y temporadas | Descender estudiante de liga cuando corresponda | Mover al estudiante a la liga inferior de acuerdo con las reglas de temporada. | No tenido en cuenta | No | NaN |
| RF-28 | Ligas y temporadas | Consultar ranking de la temporada | Mostrar posición, puntos, liga y periodo de actualización. | Si | Si | NaN |
| RF-29 | Clanes | Crear clan | Permitir crear un grupo competitivo y asignar a su creador como líder. | SI | SI | NaN |
| RF-30 | Clanes | Invitar estudiante a un clan | Enviar una invitación a un estudiante que no pertenezca a otro clan. | No | No | NaN |
| RF-31 | Clanes | Aceptar o rechazar invitación a clan | Registrar la respuesta y vincular al estudiante solo cuando acepte. | Si | Si pero muy superficial | NaN |
| RF-32 | Clanes | Limitar el clan a máximo cinco integrantes | Bloquear nuevas incorporaciones al alcanzar el límite permitido. | Si | Si | NaN |
| RF-33 | Clanes | Abandonar un clan | Retirar al integrante y reasignar liderazgo cuando sea necesario. | Si | Si pero no lo de reasignar | NaN |
| RF-34 | Clanes | Publicar reto colectivo semanal | Definir objetivo, vigencia, reglas y recompensa del reto. | Si | Si | NaN |
| RF-35 | Clanes | Acumular progreso colectivo del clan | Sumar los aportes válidos de sus integrantes durante el reto. | SI | SI | NaN |
| RF-36 | Clanes | Calcular resultado de reto entre clanes | Comparar el progreso de los clanes participantes al cierre. | Si | Se suma todo al tiempo | NaN |
| RF-37 | Clanes | Publicar resultados semanales de clanes | Mostrar posiciones, puntajes y resultado del reto. | Si | Si se ve el ranking dentro y fuera del clan | NaN |
| RF-38 | Clanes | Aplicar ascenso o descenso del clan según el resultado | Actualizar su nivel competitivo conforme a las reglas. | No | No | NaN |
| RF-39 | Gimnasio ECI | Reportar ocupación actual del gimnasio | Permitir registrar el nivel de ocupación observado. | SI | Si pero deberia haber una seccion dedidaca al gym | NaN |
| RF-40 | Gimnasio ECI | Consultar ocupación reportada del gimnasio | Mostrar el estado vigente y la hora de la última actualización. | Si | Si pero deberia haber una seccion dedidaca al gym | NaN |
| RF-41 | Gimnasio ECI | Actualizar mapa colaborativo de ocupación | Consolidar reportes recientes para representar las zonas ocupadas. | Si | Si | NaN |
| RF-42 | Gimnasio ECI | Confirmar asistencia de un estudiante por parte del entrenador | Validar la visita para habilitar los puntos asociados. | No | No | NaN |
