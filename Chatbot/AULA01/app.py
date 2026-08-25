import streamlit as st
from langchain_groq import ChatGroq
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.messages import AIMessage, HumanMessage
from dotenv import load_dotenv

load_dotenv(override=True)

st.set_page_config(
    page_title="TechStore Assistente 🤖",
    page_icon="💻"
)

st.title("💻 TechStore - Assistente Virtual")

id_modelo = "openai/gpt-oss-20b"

def load_llm():
    return ChatGroq(
        model=id_modelo,
        temperature=0.3,
        max_retries=2,
        max_tokens=None,
        timeout=None
    )

llm = load_llm()

def responder_usuario(pergunta):
    template = ChatPromptTemplate.from_messages(
        [("system",
          """
            Você é um assistente virtual de uma loja de informática chamada TechStore.

            Sua função é atender clientes que têm dúvidas sobre produtos e serviços da loja.

            A loja trabalha com:
            - notebooks;
            - computadores;
            - monitores;
            - teclados;
            - mouses;
            - impressoras;
            - acessórios;
            - manutenção de computadores;
            - formatação;
            - instalação de programas;
            - upgrade de memória e SSD.

            Regras:
            - Responda em português.
            - Seja educado, claro e objetivo.
            - Não invente preços.
            - Se o cliente perguntar por preço, diga que é necessário consultar a equipe de vendas.
            - Se o cliente pedir uma recomendação, faça uma sugestão geral com base na necessidade informada.
            - Se faltar informação, faça uma pergunta simples para entender melhor.
          """),
        ("human", "{pergunta}")]
    )
    chain = template | llm | StrOutputParser()

    resposta = chain.invoke({
        "pergunta" : pergunta
    })

    return resposta

if "mensagens" not in st.session_state:
    st.session_state.mensagens = [
        AIMessage(content="Olá! Sou o assisntente virtual da TechStore. Como posso ajudá-lo?")
    ]

for mensagem in st.session_state.mensagens:
    if isinstance(mensagem, AIMessage):
        with st.chat_message("assistant"):
            st.write(mensagem.content)
    elif isinstance(mensagem, HumanMessage):
        with st.chat_message("user"):
            st.write(mensagem.content)

pergunta = st.chat_input("Digite sua mensagem...")

if pergunta:
    st.session_state.mensagens.append(HumanMessage(content=pergunta))

    with st.chat_message("user"):
        st.write(pergunta)

    with st.chat_message("assistant"):
        try:
            resposta = responder_usuario(pergunta)
            st.write(resposta)

            st.session_state.mensagens.append(AIMessage(content=resposta))

        except Exception as e:
            st.error(f"Erro: {e}")