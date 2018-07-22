# GoTwitter



Este projeto foi um teste desenvolvido para a empresa [NEXFAR](https://nexfar.com.br/).
Consite em aplicativo para android nativo no qual consulta a timeline, home e faz um post no twitter usando a api to twitter.
Para este projeto eu usei:

  - [PICASSO](http://square.github.io/picasso/)
  - [Ion](https://github.com/koush/ion)
  - [Ttwitter4j](http://twitter4j.org/en/code-examples.html)
  - [Drawer-Behavior](https://android-arsenal.com/details/1/6239)
  - [CircleImageView](https://github.com/hdodenhof/CircleImageView)
  
  
Usei  o picasso por ser uma lib simples e tratar as imagens cacheados usei o Ion para poder consultar os meus ultimos 20 twitters na, relidade poderia e deveria ter usado ela para , postar, obter dados da home, da timeline, porém sempre obtive um bad request após muitas pesquisas, acabei usando a twitter4j no qual facilita muito o post e obter os dados da home, timeline, optei por usar as duas, a Ion já faz o trabalho fora da UIThread porém a Twitter4j eu usei uma Thread separada, não optei pelo pela AsyncTask por simples economia de código   o  CircleImageView para dar um aredondamento na imagem, e o Drawer-Behavior para dar um acabamento legal no menu.
 

#  minSdkVersion 14

 Ao executar o aplicativo uma splash ser'á apresentada com um imagem do twitter, indo para a main que verifica se existe conexão pela internt or se tratar de um primeiro acesso faz se necessário o uso da rede, na main o Ion entra em ação pegando os dados do meu perfil e os meu 20 twitters pego os 10 e coloco no SQLite e armazeno os demais em uma lista no SharedPreference para otimizara o processamento, isso tudo feito em background um fragment é exibido da minha home usando a api   Twitter4j que segue a mesma ideia de pegar os meus dados eu armazeno em lista para não ter que atualizar sempre e fico mantendo em bacground a sincronia dos dados, o aplicativo torna-se operavel offline apenas avisando que não obtivemos uma conexao e estamos pegando os dados do cahce


Você pode ver um video do app ao clicar abaixo:
  - [YOUTUBE](https://www.youtube.com/watch?v=KL6Kxq8wBb8)
   
     [![](https://i9.ytimg.com/vi/KL6Kxq8wBb8/default.jpg?sqp=CKTe09oF&rs=AOn4CLD8abphomeRYVnwR0488sZn6U05dw)](https://www.youtube.com/watch?v=KL6Kxq8wBb8)
  
 Em caso de dúvidas entre em contato: 
    * paulosoujava@gmail.com
    * (48) 996297813



License
----

MIT


**Free Software, Hell Yeah!**

